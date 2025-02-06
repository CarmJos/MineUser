package cc.carm.lib.minecraft.user;

import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.JarResourceUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public interface MineUserPlatform {

    @NotNull File getPluginFolder();

    @NotNull Logger getLogger();

    boolean isRedisAvailable();

    default void outputInfo(InputStream fileStream, Consumer<String> messageConsumer) {
        Optional.ofNullable(JarResourceUtils.readResource(fileStream))
                .map(v -> ColorParser.parse(Arrays.asList(v)))
                .ifPresent(list -> list.forEach(messageConsumer));
    }

    @NotNull UUID translatePlayer(@NotNull Object playerObject) throws IllegalArgumentException;


}
