package org.minecart.more_formula.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;

public interface MoreFormulaEvents {
    EventGroup GROUP = EventGroup.of("MoreFormulaEvents");

    EventHandler REGISTER_TIER = GROUP.startup("registerTier", () -> RegisterTierEvent.class);

    static void postAll() {
        REGISTER_TIER.post(ScriptType.STARTUP, new RegisterTierEvent());
    }
}
