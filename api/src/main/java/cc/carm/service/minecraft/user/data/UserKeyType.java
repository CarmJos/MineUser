package cc.carm.service.minecraft.user.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class UserKeyType<T> {

    public static final UserKeyType<Long> ID = new UserKeyType<Long>("id") {
        @Override
        public Long extract(@NotNull UserKey key) {
            return key.id();
        }

        @Override
        public boolean validate(@NotNull Object input) {
            return input instanceof Number;
        }

        @Override
        public boolean match(@NotNull Long data, @NotNull Object input) {
            return input instanceof Number && data == ((Number) input).longValue();
        }
    };

    public static final UserKeyType<UUID> UUID = new UserKeyType<UUID>("uuid") {
        @Override
        public UUID extract(@NotNull UserKey key) {
            return key.uuid();
        }

        @Override
        public boolean validate(@NotNull Object input) {
            return input instanceof UUID || input instanceof String;
        }

        @Override
        public boolean match(@NotNull UUID data, @NotNull Object input) {
            if (input instanceof UUID) {
                return data.equals(input);
            } else if (input instanceof String) {
                return data.toString().equals(input);
            } else return false;
        }

    };
    public static final UserKeyType<String> NAME = new UserKeyType<String>("name") {
        @Override
        public String extract(@NotNull UserKey key) {
            return key.name();
        }

        @Override
        public boolean validate(@NotNull Object input) {
            return input instanceof String;
        }

        @Override
        public boolean match(@NotNull String data, @NotNull Object input) {
            if (input instanceof String) {
                return data.equals(input);
            } else return data.equals(input.toString());
        }
    };

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
     */
    private UserKeyType(@NotNull String dataKey) {
        this.dataKey = dataKey;
    }

    public String dataKey() {
        return dataKey;
    }

    public abstract T extract(@NotNull UserKey key);

    public abstract boolean validate(@NotNull Object input);

    public abstract boolean match(@NotNull T data, @NotNull Object input);

    public boolean match(@NotNull UserKey key, @NotNull Object input) {
        return match(extract(key), input);
    }

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