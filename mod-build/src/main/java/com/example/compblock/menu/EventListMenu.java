package com.example.compblock.menu;

import com.example.compblock.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class EventListMenu extends AbstractContainerMenu {

    private final BlockPos blockPos;
    private final ContainerData data;

    public EventListMenu(int containerId, Inventory playerInventory, BlockPos blockPos, ContainerData data) {
        super(ModMenuTypes.EVENT_LIST_MENU.get(), containerId);
        this.blockPos = blockPos;
        this.data = data;
        addDataSlots(data);
    }

    public EventListMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos(), new SimpleContainerData(1));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= 8) return false;
        int states = data.get(0);
        states ^= (1 << id);
        data.set(0, states);
        return true;
    }

    public boolean isEventActive(int index) {
        return (data.get(0) & (1 << index)) != 0;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5) < 64.0;
    }
}
