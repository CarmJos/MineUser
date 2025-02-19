package cc.carm.service.minecraft.user.data;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class UserKeyType<T> {

    public static final UserKeyType<Long> ID = new UserKeyType<>(
            Long.class, "id", UserKey::id,
            (id, data) -> data instanceof Number num && id.equals(num.longValue())
    ) {
        @Override
        public boolean validate(@NotNull Object data) {
            return data instanceof Number;
        }
    };
    public static final UserKeyType<UUID> UUID = new UserKeyType<>(UUID.class, "uuid", UserKey::uuid);
    public static final UserKeyType<String> NAME = new UserKeyType<>(String.class, "name", UserKey::name);

    private static final @NotNull UserKeyType<?>[] VALUES = new UserKeyType[]{ID, UUID, NAME};

    public static @NotNull UserKeyType<?>[] values() {
        return VALUES;
    }

    protected final @NotNull String dataKey;

    protected final @NotNull Class<T> type;
    protected final @NotNull Function<UserKey, T> getter;
    protected final @NotNull BiPredicate<T, Object> matcher;


    /**
     * Value types in the {@link UserKey}.
     *
     * @param type    The original type of the value.
     * @param dataKey The key in the data map.
     */
    private UserKeyType(@NotNull Class<T> type, @NotNull String dataKey,
                        @NotNull Function<UserKey, T> getter) {
        this.dataKey = dataKey;
        this.type = type;
        this.getter = getter;
        this.matcher = Objects::equals;
    }


    /**
     * Value types in the {@link UserKey}.
     *
     * @param type    The original type of the value.
     * @param dataKey The key in the data map.
     * @param matcher The matcher to validate the value.
     */
    private UserKeyType(@NotNull Class<T> type, @NotNull String dataKey,
                        @NotNull Function<UserKey, T> getter,
                        @NotNull BiPredicate<T, Object> matcher) {
        this.dataKey = dataKey;
        this.type = type;
        this.getter = getter;
        this.matcher = matcher;
    }

    public String dataKey() {
        return dataKey;
    }

    public T extract(@NotNull UserKey key) {
        return getter.apply(key);
    }

    public boolean match(@NotNull UserKey key, @NotNull Object input) {
        return validate(input) && matcher.test(extract(key), input);
    }

    public boolean validate(@NotNull Object data) {
        return type.isInstance(data);
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