package cc.carm.service.minecraft.user.data;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum UserKeyType {
    ID("id", Long.class, Number.class),
    UUID("uuid", java.util.UUID.class),
    NAME("name", String.class);

    final @NotNull String dataKey;
    final @NotNull Class<?> type;
    final @NotNull Class<?>[] accepted;

    /**
     * Value types in the {@link UserKey}.
     *
     * @param dataKey  The key in the data map.
     * @param type     The original type of the value.
     * @param accepted The additional accepted types of the value.
     */
    UserKeyType(@NotNull String dataKey, @NotNull Class<?> type, @NotNull Class<?>... accepted) {
        this.dataKey = dataKey;
        this.type = type;
        this.accepted = accepted;
    }

    public String dataKey() {
        return dataKey;
    }

    public boolean validate(Object data) {
        return type.isInstance(data) || Arrays.stream(accepted).anyMatch(a -> a.isInstance(data));
    }


}