package cc.carm.service.minecraft.user.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class UserKeyType<T> {

    public static final UserKeyType<Long> ID = new UserKeyType<Long>("id") {
        @Override
        public Long extract(@NotNull UserKey key) {
            return key.id();
        }

        @Override
        public boolean match(@NotNull UserKey key, @NotNull Object input) {
            if (data instanceof Number num) {
                return id == num.longValue();
            } else return false;
        }
    };
    public static final UserKeyType<UUID> UUID = new UserKeyType<>("uuid", UserKey::uuid, (uuid, data) -> {
        if (data instanceof UUID other) {
            return uuid.equals(other);
        } else if (data instanceof String str) {
            return uuid.toString().equals(str);
        } else return false;
    });
    public static final UserKeyType<String> NAME = new UserKeyType<>("name", UserKey::name, (name, data) -> {
        if (data instanceof String str) {
            return name.equals(str);
        } else return name.equals(data.toString());
    });

    private static final @NotNull UserKeyType<?>[] VALUES = new UserKeyType[]{ID, UUID, NAME};

    /**
     * Get all types of the {@link UserKey}.
     *
     * @return All types of the {@link UserKey}.
     */
    public static @NotNull UserKeyType<?>[] values() {
        return VALUES;
    }

    public static @NotNull UserKeyType<?> valueOf(@NotNull String dataKey) {
        for (UserKeyType<?> type : values()) {
            if (type.dataKey().equals(dataKey)) return type;
        }
        throw new IllegalArgumentException("Unknown data key: " + dataKey);
    }

    protected final @NotNull String dataKey;

    /**
     * Value types in the {@link UserKey}.
     *
     * @param dataKey The key in the data map.
     * @param matcher The matcher to validate the value.
     */
    private UserKeyType(@NotNull String dataKey,
                        @NotNull Function<UserKey, T> getter,
                        @NotNull BiPredicate<T, Object> matcher) {
        this.dataKey = dataKey;
    }

    public String dataKey() {
        return dataKey;
    }

    public abstract T extract(@NotNull UserKey key);

    public abstract boolean match(@NotNull UserKey key, @NotNull Object input);

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        UserKeyType<?> that = (UserKeyType<?>) object;
        return dataKey.equals(that.dataKey);
    }

    @Override
    public int hashCode() {
        return dataKey.hashCode();
    }
}