package org.minecart.more_formula.compat.kubejs;

import org.minecart.more_formula.compat.kubejs.events.MoreFormulaEvents;
import org.minecart.more_formula.compat.kubejs.wrapper.MoreFormulaKubeJSConfig;
import org.minecart.more_formula.compat.kubejs.wrapper.TierConstants;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class MoreFormulaKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void afterInit() {
        MoreFormulaEvents.postAll();
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
