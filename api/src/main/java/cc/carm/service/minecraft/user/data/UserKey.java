package cc.carm.service.minecraft.user.data;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    ":(?<name>[a-zA-Z0-9_]+)$"
    );
    public static final Pattern KEY_JSON_PATTERN = Pattern.compile(
            "^\\{\"id\":(?<id>\\d+)," +
                    "\"uuid\":\"(?<uuid>[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})\"," +
                    "\"name\":\"(?<name>[a-zA-Z0-9_]+)\"}$"
    );

    public String value(UserKeyType type) {
        return switch (type) {
            case ID -> String.valueOf(id);
            case UUID -> uuid.toString();
            case NAME -> name;
        };
    }

    public boolean match(UserKeyType type, Object param) {
        return switch (type) {
            case ID -> param instanceof Number uid && id == uid.longValue();
            case UUID -> param instanceof UUID userUUID && uuid.equals(userUUID);
            case NAME -> param instanceof String username && name.equals(username);
        };
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

    public static UserKey parse(@NotNull String formatted) {
        Matcher matcher = KEY_PATTERN.matcher(formatted);
        if (!matcher.matches()) throw new IllegalArgumentException(
                "Invalid UserKey serialized format, should be <ID>:<UUID>:<NAME>."
        );
        return new UserKey(
                Long.parseLong(matcher.group("id")),
                UUID.fromString(matcher.group("uuid")),
                matcher.group("name")
        );
    }

    public static UserKey parseJson(@NotNull String json) {
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
