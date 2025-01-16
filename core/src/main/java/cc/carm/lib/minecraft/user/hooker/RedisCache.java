package cc.carm.lib.minecraft.user.hooker;

import cc.carm.lib.minecraft.user.conf.PluginConfig;
import cc.carm.lib.minecraft.user.data.UserKey;
import cc.carm.lib.minecraft.user.data.UserKeyType;
import cc.carm.plugin.mineredis.MineRedis;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RedisCache {
    // JSON FORMAT
    private static final String JSON_FORMAT = "{\"id\":%d,\"uuid\":\"%s\",\"name\":\"%s\"}";
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{\"id\":\\d+,\"uuid\":\"[0-9a-f-]+\",\"name\":\"[a-zA-Z0-9_]+\"}");

    public static @Nullable UserKey read(UserKeyType type, Object param) {
        String key = (type == UserKeyType.ID ? "#" : "") + param.toString();
        String data = MineRedis.sync().hget(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
        if (data == null) return null;
        try {
            data = data.replace(" ", "");
            Matcher matcher = JSON_PATTERN.matcher(data);
            if (!JSON_PATTERN.matcher(data).matches()) {
                MineRedis.async().hdel(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
                return null;
            }

            return new UserKey(
                    Long.parseLong(matcher.group(1)),
                    UUID.fromString(matcher.group(2)),
                    matcher.group(3)
            );
        } catch (Exception ex) {
            MineRedis.async().hdel(PluginConfig.DATA.REDIS_KEY.getNotNull(), key);
            ex.printStackTrace();
            return null;
        }
    }

    public static void cache(UserKey key) {
        String jsonValue = String.format(JSON_FORMAT, key.id(), key.uuid(), key.name());
        Map<String, String> values = Arrays.stream(UserKeyType.values()).collect(Collectors.toMap(
                value -> (value == UserKeyType.ID ? "#" : "") + key.value(value),
                value -> jsonValue, (a, b) -> b)
        );
        MineRedis.async().hset(PluginConfig.DATA.REDIS_KEY.getNotNull(), values);
    }


}
