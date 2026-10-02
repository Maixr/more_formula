package org.minecart.more_formula;

// Config 逻辑的离线回归测试。
//
// 为什么需要它：Config 是纯静态、不依赖 Minecraft 运行时的类（只用到 ResourceLocation），
// 因此可以直接在 JVM 里跑，把「修没修好」变成可执行的断言，而不是靠肉眼看代码。
// 覆盖本次修复中的三条：热重载不累积（A）、前缀门槛进 knownTiers（C）、
// 双 map 合并后的优先级（D）。

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BooleanSupplier;

public final class ConfigTest {

    private static int passed;
    private static int failed;

    private static final Config.Source SERVER = Config.Source.SERVER;
    private static final Config.Source STARTUP = Config.Source.STARTUP;

    public static void main(String[] args) {
        // ---- D：精确命中最优先，且 SERVER 覆盖 STARTUP ----
        Config.clearAll();
        Config.addTier(rl("create:mixing/a"), 1, STARTUP);
        Config.addTier(rl("create:mixing/a"), 3, SERVER);
        check("精确命中 SERVER 优先", () -> Config.getRequiredTier(rl("create:mixing/a")) == 3);

        // ---- D：前缀最长匹配优先 ----
        Config.clearAll();
        Config.addPrefixTier("create:mixing/", 1, SERVER);
        Config.addPrefixTier("create:mixing/brass", 4, SERVER);
        check("前缀最长匹配优先", () -> Config.getRequiredTier(rl("create:mixing/brass_ingot")) == 4);
        check("短前缀仍然生效", () -> Config.getRequiredTier(rl("create:mixing/copper_ingot")) == 1);

        // ---- D：精确命中压过前缀 ----
        Config.clearAll();
        Config.addPrefixTier("create:mixing/", 1, SERVER);
        Config.addTier(rl("create:mixing/brass_ingot"), 4, SERVER);
        check("精确命中压过前缀", () -> Config.getRequiredTier(rl("create:mixing/brass_ingot")) == 4);

        // ---- D：未命中的配方不受门槛影响 ----
        check("未命中返回 0", () -> Config.getRequiredTier(rl("minecraft:bread")) == 0);

        // ---- C：只配前缀门槛时，等级必须出现在 knownTiers ----
        Config.clearAll();
        Config.addPrefixTier("create:mixing/", 2, SERVER);
        check("仅有前缀门槛时 knownTiers 含该等级", () -> Config.getKnownTiers().equals(List.of(2)));

        // ---- C：精确 + 前缀混合，去重且升序 ----
        Config.clearAll();
        Config.addTier(rl("create:pressing/x"), 3, SERVER);
        Config.addPrefixTier("create:mixing/", 2, SERVER);
        Config.addPrefixTier("create:filling/", 2, SERVER);
        Config.addTier(rl("create:pressing/y"), 3, SERVER);
        check("knownTiers 去重升序", () -> Config.getKnownTiers().equals(List.of(2, 3)));

        // ---- A：模拟 /reload —— 清 SERVER 桶后重新写入，条目数不增长 ----
        Config.clearAll();
        Config.addTier(rl("create:pressing/x"), 3, SERVER);
        Config.addPrefixTier("create:mixing/", 2, SERVER);
        int exactAfterFirst = Config.getAllTiers().size();
        for (int i = 0; i < 20; i++) {
            Config.clear(Config.Source.SERVER);          // = beforeScriptsLoaded 做的事
            Config.addTier(rl("create:pressing/x"), 3, SERVER);
            Config.addPrefixTier("create:mixing/", 2, SERVER);
        }
        check("重载 20 次后精确条目不增长", () -> Config.getAllTiers().size() == exactAfterFirst);
        check("重载后门槛值仍然正确", () -> Config.getRequiredTier(rl("create:pressing/x")) == 3
                && Config.getRequiredTier(rl("create:mixing/z")) == 2);

        // ---- A：STARTUP 桶不被 /reload 清掉 ----
        Config.clearAll();
        Config.addTier(rl("create:pressing/startup"), 4, STARTUP);
        Config.addTier(rl("create:pressing/server"), 1, SERVER);
        Config.clear(Config.Source.SERVER);
        check("reload 保留 STARTUP 条目", () -> Config.getRequiredTier(rl("create:pressing/startup")) == 4);
        check("reload 清掉 SERVER 条目", () -> Config.getRequiredTier(rl("create:pressing/server")) == 0);

        // ---- A：同一个前缀重复注册保持幂等（不因重复 append 膨胀）----
        Config.clearAll();
        for (int i = 0; i < 50; i++) {
            Config.addPrefixTier("create:mixing/", 1, SERVER);
        }
        check("重复注册同一前缀保持幂等", () -> Config.getRequiredTier(rl("create:mixing/anything")) == 1
                && Config.getKnownTiers().equals(List.of(1)));

        // ---- creative 的 -1 语义 ----
        Config.clearAll();
        Config.addTier(rl("create:pressing/creative"), Config.CREATIVE_TIER, SERVER);
        check("creative 门槛生效", () -> Config.getRequiredTier(rl("create:pressing/creative")) == -1);
        check("creative 出现在 knownTiers", () -> Config.getKnownTiers().equals(List.of(-1)));

        // ---- 非法值被忽略（0 表示无门槛，不建条目）----
        Config.clearAll();
        Config.addTier(rl("create:pressing/zero"), 0, SERVER);
        Config.addTier(rl("create:pressing/big"), 9, SERVER);
        check("0 与越界值不入表", () -> Config.getKnownTiers().isEmpty()
                && Config.getRequiredTier(rl("create:pressing/zero")) == 0);

        // ---- removeTier 跨来源生效 ----
        Config.clearAll();
        Config.addTier(rl("create:pressing/x"), 2, STARTUP);
        Config.addTier(rl("create:pressing/x"), 3, SERVER);
        Config.removeTier(rl("create:pressing/x"));
        check("removeTier 清掉两个来源", () -> Config.getRequiredTier(rl("create:pressing/x")) == 0);

        // ---- 通配符写法（带 *）与前缀等价 ----
        Config.clearAll();
        Config.addPrefixTier("create:mixing/*", 2, SERVER);
        check("带 * 的前缀当成通配", () -> Config.getRequiredTier(rl("create:mixing/abc")) == 2);

        // ---- 空/异常输入不炸 ----
        Config.clearAll();
        Config.addPrefixTier("", 2, SERVER);
        Config.addPrefixTier(null, 2, SERVER);
        check("空前缀被忽略", () -> Config.getKnownTiers().isEmpty());
        check("null 配方 id 安全", () -> Config.getRequiredTier(null) == 0);

        System.out.println();
        System.out.println("PASSED=" + passed + " FAILED=" + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static ResourceLocation rl(String id) {
        return ResourceLocation.parse(id);
    }

    private static void check(String name, BooleanSupplier condition) {
        boolean ok;
        try {
            ok = condition.getAsBoolean();
        } catch (Throwable t) {
            System.out.println("FAIL  " + name + "  (threw " + t + ")");
            failed++;
            return;
        }
        if (ok) {
            System.out.println("ok    " + name);
            passed++;
        } else {
            System.out.println("FAIL  " + name);
            failed++;
        }
    }
}
