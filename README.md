```text
   __  ____          __  __
  /  |/  (_)__  ___ / / / /__ ___ _______
 / /|_/ / / _ \/ -_) /_/ (_-</ -_) __(_-<
/_/  /_/_/_//_/\__/\____/___/\__/_/ /___/
```

# MineUser

[![version](https://img.shields.io/github/v/release/CarmJos/MineUser)](https://github.com/CarmJos/MineUser/releases)
[![License](https://img.shields.io/github/license/CarmJos/MineUser)](https://www.gnu.org/licenses/lgpl-3.0.html)
[![workflow](https://github.com/CarmJos/MineUser/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/MineUser/actions/workflows/maven.yml)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/MineUser)
![](https://visitor-badge.glitch.me/badge?page_id=MineUser.readme)

MineCraft服务器通用用户键值调取、查询接口。

## 开发

本插件/依赖库提供了一个通用的用户键记录，其格式如下

```java
record UserKey(
        long id, @NotNull UUID uuid,
        @NotNull String name
)
```
而通过 `MineUserAPI` 入口类，您可以快速操作以下方法：

- `@Nullable UserKey key(UserKeyType<?> type, Object param)`
- `@Nullable UserKey key(long id)`
- `@Nullable UserKey key(@NotNull UUID uuid)` 
- `@Nullable UserKey key(@NotNull String username)`
- `@Nullable Long uid(@NotNull String username)`
- `@Nullable Long uid(@NotNull UUID userUUID)` 
- `@Nullable String username(long id)` 
- `@Nullable String username(@NotNull UUID userUUID)`
- `@Nullable UUID uuid(long id)`
- `@Nullable UUID uuid(@NotNull String username)`

对键值对的获取有基于Redis的预先缓存，但仍然建议您在使用时进行异步操作。

### 依赖方式

#### Maven 依赖

<details>
<summary>远程库配置</summary>

```xml

<project>
    <repositories>
        <repository>
            <!--采用github依赖库，实时更新，但需要配置 (推荐) -->
            <id>MineUser</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/MineUser</url>
        </repository>
        <repository>
            <!--采用我的私人依赖库，简单方便，但可能因为变故而无法使用-->
            <id>carm-repo</id>
            <name>Carm's Repo</name>
            <url>https://repo.carm.cc/repository/maven-public/</url>
        </repository>
    </repositories>
</project>
```

</details>

<details>
<summary>通用原生依赖</summary>

```xml

<project>
    <dependencies>
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>mineuser-api</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
</project>
```

</details>

#### Gradle 依赖

<details>
<summary>远程库配置</summary>

```groovy
repositories {
    
    // 采用github依赖库，实时更新，但需要配置 (推荐)
    maven { url 'https://maven.pkg.github.com/CarmJos/MineUser' }

    // 采用我的私人依赖库，简单方便，但可能因为变故而无法使用
    maven { url 'https://repo.carm.cc/repository/maven-public/' }
}
```

</details>

<details>
<summary>通用原生依赖</summary>

```groovy
dependencies {
    api "cc.carm.lib:mineuser-api:[LATEST RELEASE]"
}
```

</details>

## 支持与捐赠

若您觉得本插件做的不错，您可以通过捐赠支持我！ 感谢您对开源项目的支持！

## 开源协议

本项目源码采用 [GNU LESSER GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/lgpl-3.0.html) 开源协议。
