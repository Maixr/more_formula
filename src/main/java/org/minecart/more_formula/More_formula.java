package org.minecart.more_formula;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(More_formula.MODID)
public class More_formula {
    public static final String MODID = "more_formula";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> INCOMPLETE_TEST_PACKAGE =
            ITEMS.registerSimpleItem("incomplete_test_package");

    public More_formula(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
