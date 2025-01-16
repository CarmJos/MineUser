package cc.carm.lib.minecraft.user;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.lib.minecraft.user.conf.PluginConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MineUserBukkit extends EasyPlugin implements MineUserPlatform {

    protected MineUserCore core;

    @Override
    protected void load() {
        log("加载基础核心...");
        try {
            this.core = new MineUserCore(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    protected boolean initialize() {

        if (PluginConfig.METRICS.getNotNull()) {
            log("启用统计数据...");
            Metrics metrics = new Metrics(this, 24469);
        }

        if (PluginConfig.UPDATE_CHECKER.getNotNull()) {
            log("开始检查更新，可能需要一小段时间...");
            log("   如不希望检查更新，可在配置文件中关闭。");
            getScheduler().runAsync(() -> this.core.checkUpdate(getDescription().getVersion()));
        } else {
            log("已禁用检查更新，跳过。");
        }

        return true;
    }

    @Override
    public boolean isDebugging() {
        return PluginConfig.DEBUG.getNotNull();
    }

    @Override
    public @NotNull File getPluginFolder() {
        return getDataFolder();
    }

    @Override
    public boolean isRedisAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("MineRedis");
    }

}