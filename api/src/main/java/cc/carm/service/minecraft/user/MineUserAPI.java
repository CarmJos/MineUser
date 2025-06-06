package cc.carm.service.minecraft.user;

import cc.carm.service.minecraft.user.data.UserKey;
import cc.carm.service.minecraft.user.data.UserKeyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class MineUserAPI {

    private MineUserAPI() {
        throw new IllegalStateException("API interface class");
    }

    public static MineUserManager manager;

    /**
     * @return 已缓存的(一般是在线的玩家)用户的键集合
     */
    public static @NotNull Set<UserKey> cached() {
        return manager.cached();
    }

    /**
     * 通过在线玩家获取缓存的用户键
     *
     * @param player   在线玩家(玩家对象)
     * @param <PLAYER> 玩家对象类型, 依赖于具体的实现
     * @return {@link UserKey} 用户键
     */
    public static <PLAYER> @NotNull UserKey playerKey(@NotNull PLAYER player) {
        return manager.playerKey(player);
    }

    /**
     * 通过指定的类型获取用户的键信息。
     *
     * @param type  键的类型
     * @param param 对应键的查询参数
     * @return {@link UserKey}, 若用户从不存在则返回null
     */
    public static <T> @Nullable UserKey key(UserKeyType<T> type, T param) {
        return manager.key(type, param);
    }

    /**
     * 通过ID获取用户的键信息。
     *
     * @param id 用户ID
     * @return {@link UserKey}, 若用户从不存在则返回null
     */
    public static @Nullable UserKey key(long id) {
        return key(UserKeyType.ID, id);
    }

    /**
     * 通过UUID获取用户的键信息。
     *
     * @param uuid 用户UUID
     * @return {@link UserKey}, 若用户从不存在则返回null
     */
    public static @Nullable UserKey key(@NotNull UUID uuid) {
        return key(UserKeyType.UUID, uuid);
    }

    /**
     * 通过用户名获取用户的键信息。
     *
     * @param username 用户名
     * @return {@link UserKey}, 若用户从不存在则返回null
     */
    public static @Nullable UserKey key(@NotNull String username) {
        return key(UserKeyType.NAME, username);
    }

    // 二次衍生

    /**
     * 通过用户名获取用户的ID。
     *
     * @param username 用户名
     * @return 用户ID, 若用户从不存在则返回null
     */
    public static @Nullable Long uid(@NotNull String username) {
        return Optional.ofNullable(key(username)).map(UserKey::id).orElse(null);
    }

    /**
     * 通过UUID获取用户的ID。
     *
     * @param userUUID 用户UUID
     * @return 用户ID, 若用户从不存在则返回null
     */
    public static @Nullable Long uid(@NotNull UUID userUUID) {
        return Optional.ofNullable(key(userUUID)).map(UserKey::id).orElse(null);
    }

    /**
     * 通过ID获取用户名。
     *
     * @param id 用户ID
     * @return 用户名, 若用户从不存在则返回null
     */
    public static @Nullable String username(long id) {
        return Optional.ofNullable(key(id)).map(UserKey::name).orElse(null);
    }

    /**
     * 通过UUID获取用户名。
     *
     * @param userUUID 用户UUID
     * @return 用户名, 若用户从不存在则返回null
     */
    public static @Nullable String username(@NotNull UUID userUUID) {
        return Optional.ofNullable(key(userUUID)).map(UserKey::name).orElse(null);
    }

    /**
     * 通过ID获取用户UUID。
     *
     * @param id 用户ID
     * @return 用户UUID, 若用户从不存在则返回null
     */
    public static @Nullable UUID uuid(long id) {
        return Optional.ofNullable(key(id)).map(UserKey::uuid).orElse(null);
    }

    /**
     * 通过用户名获取用户UUID。
     *
     * @param username 用户名
     * @return 用户UUID, 若用户从不存在则返回null
     */
    public static @Nullable UUID uuid(@NotNull String username) {
        return Optional.ofNullable(key(username)).map(UserKey::uuid).orElse(null);
    }


}
