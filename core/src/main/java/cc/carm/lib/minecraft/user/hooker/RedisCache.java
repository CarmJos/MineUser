package cc.carm.lib.minecraft.user.hooker;

import cc.carm.lib.minecraft.user.conf.PluginConfig;
import cc.carm.lib.minecraft.user.data.UserKey;
import cc.carm.lib.minecraft.user.data.UserKeyType;
import cc.carm.plugin.mineredis.MineRedis;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RedisCache {

    public static @Nullable UserKey read(UserKeyType type, Object param) {
        String key = (type == UserKeyType.ID ? "#" : "") + param.toString();
        String data = MineRedis.sync().hget(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
        if (data == null) return null;
        try {
            return UserKey.parse(data);// Remove all spaces
        } catch (Exception ex) {
            MineRedis.async().hdel(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
            return null;
        }
    }

    public static void cache(UserKey key) {
        String result = key.toString(); // Single serialize
        Map<String, String> values = Arrays.stream(UserKeyType.values()).collect(Collectors.toMap(
                value -> (value == UserKeyType.ID ? "#" : "") + key.value(value),
                value -> result, (a, b) -> b)
        );
        MineRedis.async().hset(PluginConfig.DATA.REDIS_KEY.getNotNull(), values);
    }


}
