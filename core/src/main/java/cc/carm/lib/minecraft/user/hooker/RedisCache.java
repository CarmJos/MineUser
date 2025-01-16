package cc.carm.lib.minecraft.user.hooker;

import cc.carm.lib.minecraft.user.data.UserKey;
import cc.carm.lib.minecraft.user.data.UserKeyType;
import cc.carm.plugin.mineredis.MineRedis;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisCache {
    private static final JsonParser PARSER = new JsonParser();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static @Nullable UserKey read(UserKeyType type, Object param) {
        String key = (type == UserKeyType.ID ? "#" : "") + param.toString();
        String data = MineRedis.sync().hget("user-cache", key);
        if (data == null) return null;
        try {
            JsonObject json = PARSER.parse(data).getAsJsonObject();
            return new UserKey(
                    json.get("id").getAsLong(),
                    UUID.fromString(json.get("uuid").getAsString()),
                    json.get("name").getAsString()
            );
        } catch (Exception ex) {
            MineRedis.async().hdel("user-cache", key);
            ex.printStackTrace();
            return null;
        }
    }

    public static void cache(UserKey key) {
        String jsonValue = GSON.toJson(key);
        Map<String, String> values = Arrays.stream(UserKeyType.values()).collect(Collectors.toMap(
                value -> (value == UserKeyType.ID ? "#" : "") + key.value(value),
                value -> jsonValue, (a, b) -> b)
        );
        MineRedis.async().hset("user-cache", values);
    }


}
