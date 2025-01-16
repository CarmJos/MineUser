package cc.carm.lib.minecraft.user;

import cc.carm.lib.minecraft.user.data.UserKey;
import cc.carm.lib.minecraft.user.data.UserKeyType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

interface MineUserManager {

    /**
     * 通过指定的类型获取用户的键信息。
     *
     * @param type  键的类型
     * @param param 对应键的查询参数
     * @return {@link UserKey}
     */
    @Contract("_, null -> null")
    @Nullable UserKey key(@NotNull UserKeyType type, @Nullable Object param);

    /**
     * @return 已缓存的(一般是在线的玩家)用户的键集合
     */
    @Unmodifiable
    @NotNull Set<UserKey> cached();

}
