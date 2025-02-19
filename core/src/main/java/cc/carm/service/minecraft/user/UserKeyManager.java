package cc.carm.service.minecraft.user;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.table.NamedSQLTable;
import cc.carm.plugin.minesql.MineSQL;
import cc.carm.service.minecraft.user.conf.PluginConfig;
import cc.carm.service.minecraft.user.data.UserKey;
import cc.carm.service.minecraft.user.data.UserKeyType;
import cc.carm.service.minecraft.user.hooker.RedisCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserKeyManager implements MineUserManager {

    protected static final NamedSQLTable TABLE = SQLTable.of("users", table -> {
        table.addAutoIncrementColumn(UserKeyType.ID.dataKey());
        table.addColumn(UserKeyType.UUID.dataKey(), "CHAR(36) NOT NULL");
        table.addColumn(UserKeyType.NAME.dataKey(), "VARCHAR(20)");

        table.setIndex(IndexType.UNIQUE_KEY, "idx_user_uuid", UserKeyType.UUID.dataKey());
        table.setIndex(IndexType.INDEX, "idx_user_name", UserKeyType.NAME.dataKey());
    });

    protected final @NotNull Map<UUID, UserKey> loaded = new HashMap<>();
    protected final @NotNull Cache<Object, UserKey> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    protected final MineUserPlatform platform;

    public UserKeyManager(MineUserPlatform platform) {
        this.platform = platform;
    }

    public Logger getLogger() {
        return platform.getLogger();
    }

    public boolean useRedis() {
        return PluginConfig.DATA.REDIS_SUPPORT.getNotNull() && platform.isRedisAvailable();
    }

    public boolean initTables() {
        String db = PluginConfig.DATA.DATABASE.getNotNull();
        SQLManager manager = MineSQL.getRegistry().get(db);
        if (manager == null) {
            getLogger().severe("未找到ID为 " + db + " 的数据库连接！");
            return false;
        }

        try {
            TABLE.create(manager, PluginConfig.DATA.TABLE_PREFIX.getNotNull());
            return true;
        } catch (SQLException e) {
            getLogger().severe("初始化用户表失败！");
            e.printStackTrace();
            return false;
        }
    }

    public void cache(UserKey key) {
        loaded.put(key.uuid(), key);
        if (useRedis()) RedisCache.cache(key);
    }

    public void remove(UUID uuid) {
        loaded.remove(uuid);
    }

    public void load(UUID uuid, String username) {
        long id = -1;
        String cachedName = null;

        try (SQLQuery query = TABLE.createQuery()
                .addCondition(UserKeyType.UUID.dataKey(), uuid)
                .setLimit(1).build().execute()) {
            ResultSet resultSet = query.getResultSet();
            if (resultSet != null && resultSet.next()) {
                id = resultSet.getInt(UserKeyType.ID.dataKey());
                cachedName = resultSet.getString(UserKeyType.NAME.dataKey());
            }
        } catch (SQLException ignore) {
        }

        if (id > 0) {
            if (PluginConfig.USER.CREATE.getNotNull()
                    && (cachedName == null || !cachedName.equals(username))) {
                TABLE.createUpdate()
                        .addColumnValue(UserKeyType.NAME.dataKey(), username)
                        .addCondition("id", id)
                        .build().execute((e, a) -> {
                            getLogger().severe("更新用户 " + username + " 的用户名失败！");
                            e.printStackTrace();
                        });
            }
            cache(new UserKey(id, uuid, username));
            return;
        }

        if (!PluginConfig.USER.CREATE.getNotNull()) return;
        try {
            id = TABLE.createInsert()
                    .setColumnNames(UserKeyType.UUID.dataKey(), UserKeyType.NAME.dataKey())
                    .setParams(uuid, username)
                    .returnGeneratedKey().execute();
            if (id > 0) {
                cache(new UserKey(id, uuid, username));
            }
        } catch (SQLException e) {
            getLogger().severe("创建新用户 " + username + " 失败！");
        }
    }

    public @Nullable UserKey getKeyFromDatabase(UserKeyType<?> type, Object param) {
        return TABLE.createQuery()
                .addCondition(type.dataKey().toLowerCase(), param)
                .setLimit(1).build().execute(query -> {
                    ResultSet resultSet = query.getResultSet();
                    if (!resultSet.next()) return null;
                    return new UserKey(
                            resultSet.getInt(UserKeyType.ID.dataKey()),
                            UUID.fromString(resultSet.getString(UserKeyType.UUID.dataKey())),
                            resultSet.getString(UserKeyType.NAME.dataKey())
                    );
                }, null, null);
    }

    @Override
    public @Unmodifiable @NotNull Set<UserKey> cached() {
        return Set.copyOf(loaded.values());
    }

    @Override
    public @NotNull UserKey playerKey(@NotNull Object onlinePlayer) {
        if (onlinePlayer instanceof Long id) {
            return Objects.requireNonNull(key(UserKeyType.ID, id));
        } else if (onlinePlayer instanceof UUID uuid) {
            return Objects.requireNonNull(key(UserKeyType.UUID, uuid));
        } else if (onlinePlayer instanceof String name) {
            return Objects.requireNonNull(key(UserKeyType.NAME, name));
        } else if (onlinePlayer instanceof UserKey key) {
            return key;
        }

        UUID translated = platform.translatePlayer(onlinePlayer);
        return Objects.requireNonNull(key(UserKeyType.UUID, translated));
    }

    @Override
    public @Nullable UserKey key(@NotNull UserKeyType<?> type, @Nullable Object param) {
        if (param == null || !type.validate(param)) return null;
        if (type == UserKeyType.UUID) { // if UUID, check loaded first
            UserKey loaded = this.loaded.get((UUID) param);
            if (loaded != null) return loaded;
        }
        try {
            return cache.get(param, () -> {
                UserKey fromLoaded = loaded.values().stream()
                        .filter(key -> key.match(type, param))
                        .findFirst().orElse(null);
                if (fromLoaded != null) return fromLoaded;

                if (useRedis()) {
                    UserKey fromRedis = RedisCache.read(type, param);
                    if (fromRedis != null) return fromRedis;
                }

                UserKey fromDB = getKeyFromDatabase(type, param);
                if (fromDB != null && useRedis()) RedisCache.cache(fromDB);
                return fromDB;
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

}
