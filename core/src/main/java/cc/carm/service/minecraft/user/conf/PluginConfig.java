package cc.carm.service.minecraft.user.conf;


import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.HeaderComments;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

public interface PluginConfig extends Configuration {

    @HeaderComments("排错模式，一般留给开发者检查问题，平常使用无需开启。")
    ConfiguredValue<Boolean> DEBUG = ConfiguredValue.of(false);

    @HeaderComments({"",
            "统计数据设定",
            "该选项用于帮助开发者统计插件版本与使用情况，且绝不会影响性能与使用体验。",
            "当然，您也可以选择在这里关闭，或在plugins/bStats下的配置文件中关闭所有插件的统计信息。"
    })
    ConfiguredValue<Boolean> METRICS = ConfiguredValue.of(true);

    @HeaderComments({"",
            "检查更新设定",
            "该选项用于插件判断是否要检查更新，若您不希望插件检查更新并提示您，可以选择关闭。",
            "检查更新为异步操作，绝不会影响性能与使用体验。"
    })
    ConfiguredValue<Boolean> UPDATE_CHECKER = ConfiguredValue.of(true);


    @HeaderComments("用户数据配置")
    interface USER extends Configuration {
        @HeaderComments({
                "是否在本实例中自动创建新用户键信息。",
                "当用户修改正版用户名，可能造成UUID对应的用户名不同，开启后也将会自动更新。",
                "关闭后，将不会自动创建新用户键以防止UUID不同导致重复创建等问题。",
                "若您的服务器运行与BungeeCord/Velocity后，建议您在BC/VC安装并启用该功能，在子服关闭。"
        })
        ConfiguredValue<Boolean> CREATE = ConfiguredValue.of(false);
    }

    @HeaderComments("数据存储配置")
    interface DATA extends Configuration {

        @HeaderComments("数据库配置，对应MineSQL中数据连接池的ID。")
        ConfiguredValue<String> DATABASE = ConfiguredValue.of("minecraft");

        @HeaderComments({"数据表前缀，用于区分不同插件的数据表。", "本插件为基础插件，一般建议留空。"})
        ConfiguredValue<String> TABLE_PREFIX = ConfiguredValue.of("");

        @HeaderComments("是否启用Redis缓存，启用后将会使用Redis缓存用户数据。")
        ConfiguredValue<Boolean> REDIS_SUPPORT = ConfiguredValue.of(true);

        @HeaderComments("Redis缓存键值，一般不用修改。")
        ConfiguredValue<String> REDIS_KEY = ConfiguredValue.of("user-cache");

    }


}
