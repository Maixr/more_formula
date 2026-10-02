package org.minecart.more_formula.compat.kubejs;

import org.minecart.more_formula.Config;
import org.minecart.more_formula.compat.kubejs.events.MoreFormulaEvents;
import org.minecart.more_formula.compat.kubejs.wrapper.MoreFormulaKubeJSConfig;
import org.minecart.more_formula.compat.kubejs.wrapper.TierConstants;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;

public class MoreFormulaKubeJSPlugin implements KubeJSPlugin {

    /**
     * 只在加载完成时跑一次（FMLLoadCompleteEvent）。启动脚本在整个游戏生命周期中只执行一次，
     * 所以这里投递的 {@code registerTier} 事件只写 STARTUP 桶。
     */
    @Override
    public void afterInit() {
        MoreFormulaEvents.postAll();
    }

    /**
     * 每次脚本装载前都会执行 —— 包括每一次 {@code /reload}。
     *
     * <p>这是修掉「无法热重载」的关键：KubeJS 重载时会重新执行服务器脚本，
     * 脚本里的 {@code .tier()} / {@code MoreFormula.setTier} 会再次写入门槛表。
     * 如果不清空上一轮的结果，条目只会越堆越多（前缀条目尤其会无限增长，
     * 因为 PREFIX 是 List 且旧实现只 append）—— 表现为「必须大退游戏才能加载新配方」。
     *
     * <p>只清 SERVER 桶，且只在服务器脚本管理器上清：
     * <ul>
     *   <li>启动脚本（{@code startup_scripts}）不会随 {@code /reload} 重跑，
     *       清掉它们反而会丢掉 {@code MoreFormulaEvents.registerTier} 注册的门槛；</li>
     *   <li>客户端脚本管理器也会触发本回调，若不加区分，客户端脚本装载会顺带抹掉服务器门槛。</li>
     * </ul>
     */
    @Override
    public void beforeScriptsLoaded(ScriptManager manager) {
        if (manager != null && manager.scriptType != null && manager.scriptType.isServer()) {
            Config.clear(Config.Source.SERVER);
        }
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(MoreFormulaEvents.GROUP);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("MoreFormula", MoreFormulaKubeJSConfig.class);
        bindings.add("Tier", TierConstants.class);
    }
}
