package cc.carm.lib.minecraft.user.conf;

import cc.carm.lib.configuration.core.Configuration;
import cc.carm.lib.configuration.core.annotation.HeaderComment;
import cc.carm.lib.configuration.core.value.ConfigValue;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;

public interface PluginConfig extends Configuration {

    @HeaderComment("排错模式，一般留给开发者检查问题，平常使用无需开启。")
    ConfigValue<Boolean> DEBUG = ConfiguredValue.of(false);

    @HeaderComment({"",
            "统计数据设定",
            "该选项用于帮助开发者统计插件版本与使用情况，且绝不会影响性能与使用体验。",
            "当然，您也可以选择在这里关闭，或在plugins/bStats下的配置文件中关闭所有插件的统计信息。"
    })
    ConfigValue<Boolean> METRICS = ConfiguredValue.of(true);

    @HeaderComment({"",
            "检查更新设定",
            "该选项用于插件判断是否要检查更新，若您不希望插件检查更新并提示您，可以选择关闭。",
            "检查更新为异步操作，绝不会影响性能与使用体验。"
    })
    ConfigValue<Boolean> UPDATE_CHECKER = ConfiguredValue.of(true);


    @HeaderComment("用户数据配置")
    interface USER extends Configuration {
        @HeaderComment({
                "是否在本实例中自动创建新用户键信息。",
                "当用户修改正版用户名，可能造成UUID对应的用户名不同，开启后也将会自动更新。",
                "关闭后，将不会自动创建新用户键以防止UUID不同导致重复创建等问题。",
                "若您的服务器运行与BungeeCord/Velocity后，建议您在BC/VC安装并启用该功能，在子服关闭。"
        })
        ConfigValue<Boolean> CREATE = ConfiguredValue.of(false);
    }

    @HeaderComment("数据存储配置")
    interface DATA extends Configuration {

        @HeaderComment("数据库配置，对应MineSQL中数据连接池的ID。")
        ConfigValue<String> DATABASE = ConfiguredValue.of("minecraft");

        @HeaderComment({"数据表前缀，用于区分不同插件的数据表。", "本插件为基础插件，一般建议留空。"})
        ConfigValue<String> TABLE_PREFIX = ConfiguredValue.of("");

        @HeaderComment("是否启用Redis缓存，启用后将会使用Redis缓存用户数据。")
        ConfigValue<Boolean> REDIS_SUPPORT = ConfiguredValue.of(true);

    }


}
