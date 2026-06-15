package com.example.compblock.block;

import com.example.compblock.blockentity.CompBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CompBlock extends BaseEntityBlock {

    public static final MapCodec<CompBlock> CODEC = simpleCodec(CompBlock::new);

    /** Horizontal facing direction — which way the screen is looking. */
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CompBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    // ── Block state ───────────────────────────────────────────────────────────

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * When the player places the block, the screen faces TOWARD the player
     * (same convention as vanilla furnaces).
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // ── Block entity ──────────────────────────────────────────────────────────

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompBlockEntity(pos, state);
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult result) {
        if (!level.isClientSide) {
            MenuProvider menuProvider = state.getMenuProvider(level, pos);
            if (menuProvider != null && player instanceof ServerPlayer sp) {
                sp.openMenu(menuProvider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
