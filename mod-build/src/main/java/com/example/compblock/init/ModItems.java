package com.example.compblock.init;

import com.example.compblock.CompBlockMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CompBlockMod.MODID);

    static {
        ITEMS.registerSimpleBlockItem(ModBlocks.COMP_BLOCK);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
