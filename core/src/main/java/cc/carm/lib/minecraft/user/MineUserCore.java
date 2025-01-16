package cc.carm.lib.minecraft.user;

import cc.carm.lib.configuration.EasyConfiguration;
import cc.carm.lib.configuration.yaml.YAMLConfigProvider;
import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.JarResourceUtils;
import cc.carm.lib.githubreleases4j.GithubReleases4J;
import cc.carm.lib.minecraft.user.conf.PluginConfig;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MineUserCore {

    public static final String REPO_OWNER = "CarmJos";
    public static final String REPO_NAME = "MineUsers";

    protected final MineUserPlatform platform;

    protected final YAMLConfigProvider configProvider;
    protected final UserKeyManager manager;

    public MineUserCore(MineUserPlatform platform) {
        this.platform = platform;

        getLogger().info("加载配置文件...");
        this.configProvider = EasyConfiguration.from(new File(platform.getPluginFolder(), "config.yml"));
        this.configProvider.initialize(PluginConfig.class);

        this.manager = new UserKeyManager(platform);

        getLogger().info("初始化数据表...");
        boolean success = this.manager.initTables();
        if (!success) {
            getLogger().severe("数据表初始化失败，请检查配置文件是否正确。");
            throw new RuntimeException("数据表初始化失败");
        }

        getLogger().info("初始化MineUser API...");
        MineUserAPI.plugin = this.manager;
    }

    public Logger getLogger() {
        return platform.getLogger();
    }

    void outputInfo(InputStream fileStream, Consumer<String> messageConsumer) {
        Optional.ofNullable(JarResourceUtils.readResource(fileStream))
                .map(v -> ColorParser.parse(Arrays.asList(v)))
                .ifPresent(list -> list.forEach(messageConsumer));
    }


    public void checkUpdate(String currentVersion) {
        Logger logger = getLogger();

        Integer behindVersions = GithubReleases4J.getVersionBehind(REPO_OWNER, REPO_NAME, currentVersion);
        String downloadURL = GithubReleases4J.getReleasesURL(REPO_OWNER, REPO_NAME);
        if (behindVersions == null) {
            logger.severe("检查更新失败，请您定期查看插件是否更新，避免安全问题。");
            logger.severe("下载地址 " + downloadURL);
        } else if (behindVersions < 0) {
            logger.severe("检查更新失败! 当前版本未知，请您使用原生版本以避免安全问题。");
            logger.severe("最新版下载地址 " + downloadURL);
        } else if (behindVersions > 0) {
            logger.warning("发现新版本! 目前已落后 " + behindVersions + " 个版本。");
            logger.warning("最新版下载地址 " + downloadURL);
        } else {
            logger.info("检查完成，当前已是最新版本。");
        }
    }

}
