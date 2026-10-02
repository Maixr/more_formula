# 构建说明

> **结论先行**：本工程**在这台开发机上不能用 Gradle 构建**（原因见下），因此提供了一套
> 手工 javac 流程。如果你的机器能正常访问 NeoForge/KubeJS 的 Maven 仓库，直接用 Gradle 即可，
> 本节末尾的手工流程只是**备选路径**。

---

## 一、标准方式：Gradle（推荐，前提是网络可达）

```bash
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows
```

产物：`build/libs/more_formula-<版本>.jar`

需要 Java 21。

---

## 二、备选方式：手工 javac（本机无 Gradle 仓库时用）

### 为什么本机不能用 Gradle

本机对以下域名的 TLS 连接不可用（`curl` 返回 `000`）：

| 域名 | 用途 | 状态 |
|---|---|---|
| `maven.neoforged.net` | NeoForge / ModDevGradle 插件 | ✗ 不可达 |
| `maven.latvian.dev` | KubeJS | ✗ 不可达 |
| `plugins.gradle.org` | Gradle 插件门户 | ✗ 不可达 |
| `services.gradle.org` | Gradle 发行包下载 | ✗ 不可达 |

可达的：`maven.createmod.net`、`maven.ithundxr.dev`、`maven.blamejared.com`、
`repo1.maven.org`、`jitpack.io`。

Gradle 遇到网络错误是**致命**的（不会自动跳到下一个仓库），所以 ModDevGradle
一上来就解析不了，整条路走不通。

### 原理

复用 Gradle 缓存里**已经解好的 Minecraft + NeoForge 合并包**：

```
<GRADLE_USER_HOME>/caches/ng_execute/<hash>/outputs.jar
```

（`GRADLE_USER_HOME` 默认 `~/.gradle`，可用环境变量覆盖。）

这个合并包是之前某次**联网**构建留下的产物。**如果该缓存被清空，需要先做一次能联网的 Gradle 构建来重建它。**

### 用法

```powershell
cd <工程目录>
.\build_run.cmd
```

流程：

```
build_run.cmd
  └─ node tools\build.js          ← 全部构建逻辑
       ├─ node tools\genargs.js    → 生成 build\args.txt（javac 的 @argfile）
       ├─ javac @build\args.txt    → 编译到 build\classes
       └─ node tools\pack.js       → 展开 mods.toml 占位符 + 打包
                                      → build\libs\more_formula-<版本>.jar
```

`build_run.cmd` 里若未设置 `JAVA_HOME`，会默认指向
`C:\Program Files\Java\jdk-21.0.11`；**路径不同请自行修改**，或先设好环境变量。

---

## 三、四个必踩的坑（都已固化在 `tools/*.js` 里）

1. **javac 的 @argfile 会把反斜杠当转义符。**
   所有路径统一转成正斜杠。不这么做会报一堆莫名其妙的
   `package net.minecraft.resources does not exist`，极具误导性。

2. **@argfile 按平台默认编码读取。**
   类路径里不能出现非 ASCII 路径。整合包里的 JEI jar 名字形如
   `[JEI物品管理器] jei-...jar`，所以要先复制成纯 ASCII 名放进 `build/deps/`。

3. **别用 PowerShell 的 `>` 去接 `node tools/genargs.js` 的输出。**
   Windows PowerShell 的 `>` 写的是 **UTF-16LE**，javac 按平台编码读 argfile 会看到乱码，
   现象与坑 1 几乎一样，极难分辨。
   `tools/build.js` 因此直接在 Node 里 `writeFileSync(..., 'utf8')`，不经过任何 shell 重定向。

4. **`ng_execute` 下有多个 `outputs.jar`**（对应不同 NeoForge 版本）。
   按体积挑是不确定的 —— 一旦挑到别的版本，编译会因 API 差异失败。
   `tools/genargs.js` 固定用已验证的那一份（按目录哈希 `KNOWN_GOOD_MERGED_HASH` 定位），
   文件不存在时才回退挑选。

另外：`-Dfile.encoding` 在 PowerShell 里会被吃掉，要传就通过 `JAVA_TOOL_OPTIONS`。

---

## 四、依赖来自哪里

### 随仓库分发（`libs/`）—— clone 下来即可编译

| 依赖 | 文件 |
|---|---|
| Create 6.0.10 | `libs/create-1.21.1-6.0.10.jar` |
| Create: More Machines 2.7 | `libs/createmoremachines-1.21.1-2.7.jar` |
| KubeJS-Create | `libs/kubejs-create-neoforge-2101.3.1-build.18.jar` |
| KubeJS 本体 | `libs/kubejs-neoforge-2101.7.2-build.377.jar` |
| rhino（KubeJS 依赖） | `libs/rhino-2101.2.8-build.91.jar` |
| JEI | `libs/jei-1.21.1-neoforge-19.57.0.449.jar` |

### 构建时就地生成（`build/deps/`）—— 离线，不需要手动准备

| 依赖 | 来源 |
|---|---|
| ponder / flywheel / Registrate | 从 `libs/create-*.jar` 的 `META-INF/jarjar/` 里解出 |

`tools/build.js` 会先跑 `tools/fetch-deps.js` 自动完成这一步；
`build/deps/` 不进仓库（由 `build/` 规则忽略），所以**每次构建都现解**，
但完全不联网。

### 来自 Gradle 缓存（不随仓库分发）

| 依赖 | 来源 |
|---|---|
| Minecraft + NeoForge 21.1.248 | `ng_execute/<hash>/outputs.jar` |
| asm / guava / brigadier / netty / log4j 等 | `modules-2/files-2.1`，由 `tools/genargs.js` 按坐标解析 |

`GRADLE_USER_HOME` 默认 `~/.gradle`，可用环境变量覆盖。
**这些必须来自一次成功的联网 Gradle 构建** —— 见第二节开头的说明。

> 版本说明：`libs/` 里的 KubeJS / JEI 比 `gradle.properties` / `build.gradle`
> 里声明的更新（声明 KubeJS build.285 / JEI 19.44.0.401，实际随仓库分发的是
> build.377 / 19.57.0.449）。已用 `javap` 逐一核对过本工程用到的 API 在两个版本间签名一致。

---

## 五、测试

```powershell
node tools\run_tests.js
```

`src/test/java/.../ConfigTest.java` 是门槛表（`Config`）逻辑的离线回归测试，
共 19 条断言，覆盖：热重载不累积、前缀门槛进 `getKnownTiers`、优先级规则、
创作级语义、非法值处理等。

它只用到 `ResourceLocation`，不依赖游戏运行时，因此可以直接在 JVM 里跑。

---

## 六、可复现性

清空 `build/classes`、`build/libs`、`build/stage` 后重新构建，
产物内**每个文件的内容哈希完全一致**（只有 zip 条目的时间戳不同，故整体 md5 会变）。

在一台「只有仓库 + Gradle 缓存、没有 `build/`」的干净副本上验证过：
`build_run.cmd` 可直接跑通，产物与开发机**逐文件一致**。
