package com.example.compblock.init;

import com.example.compblock.CompBlockMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CompBlockMod.MODID);

    public static final Supplier<CreativeModeTab> COMPBLOCK_TAB =
            CREATIVE_MODE_TABS.register("compblock_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.compblock.compblock_tab"))
                            .icon(() -> ModBlocks.COMP_BLOCK.get().asItem().getDefaultInstance())
                            .displayItems((params, output) -> {
                                output.accept(ModBlocks.COMP_BLOCK.get());
                            })
                            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
