package cc.carm.lib.minecraft.user;

import cc.carm.lib.minecraft.user.conf.PluginConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public class MineUserBungee extends Plugin implements MineUserPlatform {

    protected MineUserCore core;

    @Override
    public void onLoad() {
        getLogger().info("加载基础核心...");
        this.core = new MineUserCore(this);
    }

    @Override
    public void onEnable() {
        outputInfo();

        if (PluginConfig.METRICS.getNotNull()) {
            getLogger().info("启用统计数据...");
            Metrics metrics = new Metrics(this, 24469);
        }

        if (PluginConfig.UPDATE_CHECKER.getNotNull()) {
            getLogger().info("开始检查更新，可能需要一小段时间...");
            getLogger().info("   如不希望检查更新，可在配置文件中关闭。");
            ProxyServer.getInstance().getScheduler().runAsync(
                    this, () -> this.core.checkUpdate(getDescription().getVersion())
            );
        } else {
            getLogger().info("已禁用检查更新，跳过。");
        }
    }


    @Override
    public @NotNull Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public boolean isRedisAvailable() {
        return ProxyServer.getInstance().getPluginManager().getPlugin("MineRedis") != null;
    }

    @Override
    public @NotNull File getPluginFolder() {
        return getDataFolder();
    }

    @SuppressWarnings("deprecation")
    public void outputInfo() {
        outputInfo(this.getResourceAsStream("PLUGIN_INFO"), s -> ProxyServer.getInstance().getConsole().sendMessage(s));
    }

}