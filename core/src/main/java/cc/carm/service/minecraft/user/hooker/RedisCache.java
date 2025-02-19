package cc.carm.service.minecraft.user.hooker;

import cc.carm.plugin.mineredis.MineRedis;
import cc.carm.service.minecraft.user.conf.PluginConfig;
import cc.carm.service.minecraft.user.data.UserKey;
import cc.carm.service.minecraft.user.data.UserKeyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RedisCache {

    private RedisCache() {
    }

    public static @Nullable UserKey read(@NotNull UserKeyType<?> type, Object param) {
        String key = (type == UserKeyType.ID ? "#" : "") + param.toString();
        String data = MineRedis.sync().hget(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
        if (data == null) return null;
        try {
            return UserKey.fromString(data);// Remove all spaces
        } catch (Exception ex) {
            MineRedis.async().hdel(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
            return null;
        }
    }

    public static void cache(UserKey key) {
        String result = key.toString(); // Singleton serialization
        Map<String, String> values = Arrays.stream(UserKeyType.values()).collect(Collectors.toMap(
                value -> (value == UserKeyType.ID ? "#" : "") + key.value(value),
                value -> result, (a, b) -> b)
        );
        MineRedis.async().hset(PluginConfig.DATA.REDIS_KEY.getNotNull(), values);
    }


}
