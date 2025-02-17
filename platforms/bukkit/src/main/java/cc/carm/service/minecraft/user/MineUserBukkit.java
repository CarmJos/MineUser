package cc.carm.service.minecraft.user;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.service.minecraft.user.conf.PluginConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class MineUserBukkit extends EasyPlugin implements MineUserPlatform, Listener {

    protected MineUserCore core;
    protected boolean enabled = true;

    @Override
    protected void load() {
        log("加载基础核心...");
        try {
            this.core = new MineUserCore(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.enabled = false;
        }
    }

    @Override
    protected boolean initialize() {
        if (!this.enabled) {
            log("插件初始化失败，已禁用。");
            return false;
        }

        registerListener(this);

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

    @Override
    public @NotNull UUID translatePlayer(@NotNull Object playerObject) throws IllegalArgumentException {
        if (!(playerObject instanceof OfflinePlayer player)) {
            throw new IllegalArgumentException("Only a player can provide a UserKey.");
        }
        return player.getUniqueId();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        this.core.manager.load(event.getUniqueId(), event.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreLoginMonitor(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.core.manager.remove(event.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.core.manager.remove(event.getPlayer().getUniqueId());
    }


}