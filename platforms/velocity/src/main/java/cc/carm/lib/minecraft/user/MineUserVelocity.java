package cc.carm.lib.minecraft.user;

import cc.carm.lib.minecraft.user.conf.PluginConfig;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "mineuser", name = "MineUser",
        description = "MineUser For Velocity",
        url = "https://github.com/CarmJos/MineUser",
        authors = {"CarmJos"}
)
public class MineUserVelocity implements MineUserPlatform {

    private final ProxyServer server;
    private final Logger logger;
    private final File dataFolder;

    private final Metrics.Factory metricsFactory;

    protected MineUserCore core;

    @Inject
    public MineUserVelocity(ProxyServer server, Logger logger,
                            @DataDirectory Path dataDirectory,
                            Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataDirectory.toFile();
        this.metricsFactory = metricsFactory;

        getLogger().info("加载基础核心...");
        this.core = new MineUserCore(this);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onInitialize(ProxyInitializeEvent event) {
        outputInfo();


        if (PluginConfig.METRICS.getNotNull()) {
            getLogger().info("启用统计数据...");
            Metrics metrics = this.metricsFactory.make(this, 24469);
        }


        if (PluginConfig.UPDATE_CHECKER.getNotNull()) {
            getLogger().info("开始检查更新，可能需要一小段时间...");
            getLogger().info("   如不希望检查更新，可在配置文件中关闭。");
            server.getScheduler().buildTask(this, () -> this.core.checkUpdate(getVersion())).schedule();
        } else {
            getLogger().info("已禁用检查更新，跳过。");
        }

    }

    public ProxyServer getServer() {
        return server;
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isRedisAvailable() {
        return getServer().getPluginManager().isLoaded("mineredis");
    }

    public String getVersion() {
        VersionReader versionReader = new VersionReader();
        return versionReader.get("version");
    }

    @Override
    public @NotNull File getPluginFolder() {
        return this.dataFolder;
    }

    public void outputInfo() {
        outputInfo(
                this.getClass().getResourceAsStream("PLUGIN_INFO"),
                s -> getServer().getConsoleCommandSource().sendMessage(Component.text(s))
        );
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent event) {
        this.core.manager.load(event.getPlayer().getUniqueId(), event.getPlayer().getUsername());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisconnect(LoginEvent event) {
        if (event.getResult().isAllowed()) return;
        this.core.manager.remove(event.getPlayer().getUniqueId());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisconnect(DisconnectEvent event) {
        this.core.manager.remove(event.getPlayer().getUniqueId());
    }

}
