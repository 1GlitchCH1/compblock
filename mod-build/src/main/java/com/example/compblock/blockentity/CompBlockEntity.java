package com.example.compblock.blockentity;

import com.example.compblock.init.ModBlockEntityTypes;
import com.example.compblock.init.ModGameRules;
import com.example.compblock.menu.EventListMenu;
import com.example.compblock.network.BloodMoonSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class CompBlockEntity extends BlockEntity implements MenuProvider {

    public static final int EVENT_COUNT = 8;

    private int eventStates = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override public int get(int index) { return index == 0 ? eventStates : 0; }

        @Override
        public void set(int index, int value) {
            if (index != 0) return;

            int old = eventStates;
            eventStates = value;
            setChanged();

            // ── Blood Moon (bit 1) ─────────────────────────────────────────
            boolean now    = (eventStates & (1 << 1)) != 0;
            boolean before = (old         & (1 << 1)) != 0;

            if (now != before && level instanceof ServerLevel sl) {
                applyBloodMoon(sl, now);
            }
        }

        @Override public int getCount() { return 1; }
    };

    public CompBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COMP_BLOCK_ENTITY.get(), pos, state);
    }

    // ── Blood Moon ────────────────────────────────────────────────────────────

    private static void applyBloodMoon(ServerLevel sl, boolean active) {
        // 1. Persist in GameRule (survives restarts; also initial-join sync source)
        sl.getGameRules()
          .getRule(ModGameRules.BLOOD_MOON_ACTIVE)
          .set(active, sl.getServer());

        // 2. Immediate packet to every connected client for real-time visual sync
        PacketDistributor.sendToAllPlayers(new BloodMoonSyncPayload(active));

        // 3. Chat announcement
        Component msg = active
                ? Component.literal("§4☽ §lКровавая Луна восходит...§r §4☽")
                : Component.literal("§8☽ Кровавая Луна зашла. ☽");

        for (ServerPlayer p : sl.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(msg);
        }
    }

    // ── Boilerplate ───────────────────────────────────────────────────────────

    public ContainerData getDataAccess() { return dataAccess; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.compblock.event_list");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EventListMenu(id, inv, this.worldPosition, this.dataAccess);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.saveAdditional(tag, reg);
        tag.putInt("EventStates", eventStates);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.loadAdditional(tag, reg);
        eventStates = tag.getInt("EventStates");
    }
}
