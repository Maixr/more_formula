# More Formula

`More Formula` 是一个适用于 NeoForge 1.21.1 的 Create 扩展模组，为 Create: More Machines 的高级机器增加配方等级门槛。

配方可以要求黄铜、下界合金、末影、超越或创造级机器才能处理。所有门槛都通过 KubeJS 配置，并兼容 KubeJS-Create 的 Create 配方脚本 API。

本mod由ai辅助开发

目前版本有个bug，体现为无法热重载/reload，必须大退游戏后才可加载新配方

## 依赖

以下依赖**全部**必需：

| 依赖 | 版本要求 |
|------|----------|
| Minecraft | 1.21.1 |
| NeoForge | 21.x |
| Create | 6.0.0 或更高版本 |
| Create: More Machines | 2.7 或更高版本 |
| KubeJS | 2101.7.2-build.285 或更高版本 |
| KubeJS-Create | 2101.3.1-build.18 或更高版本 |

## 功能

- 为 Create 配方设置机器等级门槛。
- 支持辊压、搅拌、注液器、机械手、和序列装配。
- 为 JEI 注册按等级区分的配方分类标签。
- JEI 中显示对应等级的 CMM **高级机器动画**和催化剂。
- 支持创造级专属配方。
- 支持按配方 ID 精确设置门槛和按前缀批量设置门槛。
- 不使用 TOML 配置文件。

## 等级

| 常量 | 数值 | 说明 |
|------|------|------|
| `Tier.ZERO` | `0` | 无门槛，原版 Create 机器即可处理 |
| `Tier.BRASS` | `1` | 黄铜级 |
| `Tier.NETHERITE` | `2` | 下界合金级 |
| `Tier.END` | `3` | 末影级 |
| `Tier.BEYOND` | `4` | 超越级 |
| `Tier.CREATIVE` | `-1` | 仅 CMM 创造级机器 |

普通等级遵循“当前机器等级大于等于配方门槛”的规则。创造级门槛是特殊规则：只有机器等级为 `-1` 的创造机器可以处理，其他所有机器都会被拒绝。

## 安装

1. 安装 NeoForge 1.21.1。
2. 安装 Create、Create: More Machines、KubeJS 和 KubeJS-Create。
3. 将 `more_formula` 的 JAR 文件放入游戏的 `mods` 文件夹。
4. 启动游戏。

## KubeJS 用法

在 `kubejs/server_scripts/` 下创建脚本：

```kjs
ServerEvents.recipes(event => {
    // 需要末影级压机
    event.recipes.create.pressing(
        'minecraft:iron_block',   //产物
        'minecraft:iron_ingot'    //反应物
    ).id('example:end_pressing')  //配方id
        .tier(Tier.END)           //机器等级

    // 需要下界合金级，并且必须加热
    event.recipes.create.mixing(
        'minecraft:gold_block',
        '9x minecraft:gold_ingot'
    ).heated()
        .id('example:heated_mixing')
        .tier(Tier.NETHERITE)

    // 只有创造级机器可以处理
    event.recipes.create.pressing(
        'minecraft:beacon',
        'minecraft:nether_star'
    ).id('example:creative_pressing')
        .tier(Tier.CREATIVE)
})
```
以及装配线
```kjs
………
    // 序列装配：整条装配线需要超越级
    let transitional = 'kubejs:incomplete_test_package'  //装配线名字
    event.recipes.create.sequenced_assembly(
        'minecraft:copper_block',  //产物
        'minecraft:copper_ingot',  //反应物
        [
            event.recipes.create.pressing(transitional, transitional),   //第一道工序，辊压
            event.recipes.create.filling(transitional, [transitional, Fluid.of('minecraft:water', 250)]),  //第二道工序，注液
            event.recipes.create.deploying(transitional, [transitional, 'minecraft:quartz'])  //第三道工序，机械手装配
        ]
    ).transitionalItem(transitional).loops(2).tier(Tier.BEYOND)
}
```
#### [更多kjs使用说明点我](./docs/KubeJS使用说明.md)

### 修改脚本后在游戏中执行（当前版本有bug无法热重载）：

```指令
/reload
```

## 支持的配方类型

可以设置门槛的类型：

- `create:pressing`：动力辊压机
- `create:mixing`：动力搅拌器和工作盆
- `create:compacting`：动力辊压机和工作盆
- `create:filling`：注液器
- `create:deploying`：机械手
- `create:item_application`：拿物品的机械手
- `create:sequenced_assembly`：序列装配

以下类型没有对应的 CMM 高级机器，因此不建议设置门槛：

- `create:crushing`
- `create:milling`
- `create:splashing`
- `create:cutting`
- `create:emptying`
- 流体储罐和蒸汽引擎相关配方

## 按 ID 设置门槛

不需要重写原配方，也可以直接绑定门槛：

```kjs
// 精确设置
MoreFormula.setTier(
    'create:sequenced_assembly/precision_mechanism',   //配方id
    Tier.END                                           //设置等级
)

// 为指定前缀的配方设置门槛
MoreFormula.setPrefixTier('create:mixing/', Tier.BRASS)

// 查询和移除
MoreFormula.getTier('create:mixing/brass_ingot')
MoreFormula.removeTier('create:mixing/brass_ingot')
```

也可以使用事件方式批量管理：

```kjs
MoreFormulaEvents.registerTier(event => {
    event.setTier('create:mixing/*', Tier.BRASS)
    event.setTier(
        'create:sequenced_assembly/precision_mechanism',
        Tier.BEYOND
    )
})
```

## 构建

项目使用 Gradle，Java 版本要求为 21：

```bash
./gradlew build
```

Windows：

```bat
gradlew.bat build
```

构建产物位于：

```text
build/libs/more_formula-版本号.jar
```

## 文档与许可证

- KubeJS 详细说明：[docs/KubeJS使用说明.md](docs/KubeJS使用说明.md)
- 许可证：MIT，见 [LICENSE](LICENSE)
