package com.example.compblock.init;

import com.example.compblock.CompBlockMod;
import com.example.compblock.block.CompBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CompBlockMod.MODID);

    public static final DeferredBlock<CompBlock> COMP_BLOCK = BLOCKS.registerBlock(
            "comp_block",
            CompBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
