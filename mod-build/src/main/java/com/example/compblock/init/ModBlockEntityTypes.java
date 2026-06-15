package com.example.compblock.init;

import com.example.compblock.CompBlockMod;
import com.example.compblock.blockentity.CompBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CompBlockMod.MODID);

    public static final Supplier<BlockEntityType<CompBlockEntity>> COMP_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("comp_block", () ->
                    BlockEntityType.Builder.of(CompBlockEntity::new, ModBlocks.COMP_BLOCK.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
