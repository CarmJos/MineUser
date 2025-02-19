package cc.carm.service.minecraft.user.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A key to identify a user.
 *
 * @param id   The user's id, should be unique, usually the auto-increment primary key in database.
 * @param uuid The user's uuid, should be unique, usually the uuid of the user in Minecraft.
 * @param name The user's name, may not be unique, usually the name of the user in Minecraft.
 */
public record UserKey(
        long id, @NotNull UUID uuid,
        @NotNull String name
) {

    public static @NotNull UserKey of(long id, @NotNull UUID uuid, @NotNull String name) {
        return new UserKey(id, uuid, name);
    }

    public static final Pattern KEY_PATTERN = Pattern.compile(
            "^(?<id>\\d+)" +
                    ":(?<uuid>[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})" +
                    ":(?<name>\\w+)$"
    );
    public static final Pattern KEY_JSON_PATTERN = Pattern.compile(
            "^\\{\"id\":(?<id>\\d+)," +
                    "\"uuid\":\"(?<uuid>[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})\"," +
                    "\"name\":\"(?<name>\\w+)\"}$"
    );

    /**
     * Get the value of the key by the given type.
     *
     * @param type The type of the key.
     * @param <T>  The type of the data.
     * @return The value of the key.
     * @see UserKeyType
     */
    public <T> T value(@NotNull UserKeyType<T> type) {
        return type.extract(this);
    }

    /**
     * Match the key with the given type and param.
     *
     * @param type  The type of the key.
     * @param param The param to match.
     * @param <T>   The type of the data.
     * @return Whether the key matches the param.
     */
    @Contract(pure = true, value = "_, null -> false")
    public <T> boolean match(@NotNull UserKeyType<T> type, @Nullable Object param) {
        if (param == null) return false;
        if (param instanceof UserKey userKey) return userKey.equals(this);
        return type.match(this, param);
    }

    @Override
    @SuppressWarnings("StringBufferReplaceableByString") // To be faster
    public @NotNull String toString() {
        return new StringBuilder().append(id).append(":").append(uuid).append(":").append(name).toString();
    }

    @SuppressWarnings("StringBufferReplaceableByString") // To be faster
    public @NotNull String toJson() {
        return new StringBuilder().append("{\"id\":").append(id)
                .append(",\"uuid\":\"").append(uuid)
                .append("\",\"name\":\"").append(name)
                .append("\"}").toString();
    }

    /**
     * Deserialize a UserKey from a formatted string using {@link #KEY_PATTERN}.
     *
     * @param formatted The formatted string, should be <ID>:<UUID>:<NAME>.
     * @return The deserialized UserKey.
     * @throws IllegalArgumentException If the formatted string is invalid.
     * @see #toString()
     */
    public static UserKey fromString(@NotNull String formatted) {
        Matcher matcher = KEY_PATTERN.matcher(formatted.replace(" ", ""));
        if (!matcher.matches()) throw new IllegalArgumentException(
                "Invalid UserKey serialized format, should be <ID>:<UUID>:<NAME>."
        );
        return new UserKey(
                Long.parseLong(matcher.group("id")),
                UUID.fromString(matcher.group("uuid")),
                matcher.group("name")
        );
    }

    /**
     * Deserialize a UserKey from a json string using {@link #KEY_JSON_PATTERN}.
     *
     * @param json The json string, should be {"id":<ID>,"uuid":"<UUID>","name":"<NAME>"}.
     * @return The deserialized UserKey.
     * @see #toJson()
     */
    public static UserKey fromJSON(@NotNull String json) {
        Matcher matcher = KEY_JSON_PATTERN.matcher(json.replace(" ", ""));
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid UserKey json format.");
        return new UserKey(
                Long.parseLong(matcher.group("id")),
                UUID.fromString(matcher.group("uuid")), matcher.group("name")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserKey userKey = (UserKey) o;
        return id == userKey.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
