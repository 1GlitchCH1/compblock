package com.example.compblock.screen;

import com.example.compblock.menu.EventListMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EventListScreen extends AbstractContainerScreen<EventListMenu> {

    private static final String[] EVENT_NAMES = {
        "Thunderstorm",
        "Blood Moon",
        "Meteor Shower",
        "Dragon Attack",
        "Goblin Raid",
        "Witch Awakening",
        "Earthquake",
        "Solar Eclipse"
    };

    private static final int GUI_W = 260;
    private static final int GUI_H = 230;
    private static final int ROW_H = 22;
    private static final int LIST_Y_OFFSET = 38;

    private final List<Button> toggleButtons = new ArrayList<>();

    public EventListScreen(EventListMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_W;
        this.imageHeight = GUI_H;
        this.inventoryLabelY = GUI_H + 10;
        this.titleLabelY = 7;
    }

    @Override
    protected void init() {
        super.init();
        toggleButtons.clear();

        for (int i = 0; i < EVENT_NAMES.length; i++) {
            final int idx = i;
            int btnX = leftPos + GUI_W - 96;
            int btnY = topPos + LIST_Y_OFFSET + i * ROW_H + 2;

            Button btn = Button.builder(buildLabel(idx), b -> {
                        Minecraft.getInstance().gameMode.handleInventoryButtonClick(
                                menu.containerId, idx);
                    })
                    .pos(btnX, btnY)
                    .size(82, 16)
                    .build();

            toggleButtons.add(btn);
            addRenderableWidget(btn);
        }
    }

    private Component buildLabel(int idx) {
        return menu.isEventActive(idx)
                ? Component.literal("§4▶ Deactivate")
                : Component.literal("§2▶ Activate");
    }

    private void refreshButtonLabels() {
        for (int i = 0; i < toggleButtons.size(); i++) {
            toggleButtons.get(i).setMessage(buildLabel(i));
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mx, int my) {
        int x = leftPos;
        int y = topPos;

        g.fill(x, y, x + GUI_W, y + GUI_H, 0xEE0D1B2A);
        g.fill(x + 1, y + 1, x + GUI_W - 1, y + GUI_H - 1, 0xEE101F30);

        g.fill(x, y, x + GUI_W, y + 26, 0xFF0A1628);
        g.fill(x, y + 24, x + GUI_W, y + 26, 0xFF1E6FA8);

        g.fill(x, y + 28, x + GUI_W, y + LIST_Y_OFFSET, 0xFF0D2137);
        g.fill(x, y + LIST_Y_OFFSET - 1, x + GUI_W, y + LIST_Y_OFFSET, 0xFF1E6FA8);

        for (int i = 0; i < EVENT_NAMES.length; i++) {
            int ry = y + LIST_Y_OFFSET + i * ROW_H;
            int rowColor = (i % 2 == 0) ? 0x15FFFFFF : 0x08FFFFFF;
            g.fill(x + 2, ry, x + GUI_W - 2, ry + ROW_H - 1, rowColor);

            boolean active = menu.isEventActive(i);
            int dotColor = active ? 0xFF00E676 : 0xFFFF1744;
            int dotX = x + 10;
            int dotY = ry + ROW_H / 2 - 3;
            g.fill(dotX, dotY, dotX + 6, dotY + 6, dotColor);
            g.fill(dotX + 1, dotY + 1, dotX + 3, dotY + 3, 0x66FFFFFF);
        }

        g.hLine(x, x + GUI_W - 1, y, 0xFF1E6FA8);
        g.hLine(x, x + GUI_W - 1, y + GUI_H - 1, 0xFF1E6FA8);
        g.vLine(x, y, y + GUI_H - 1, 0xFF1E6FA8);
        g.vLine(x + GUI_W - 1, y, y + GUI_H - 1, 0xFF1E6FA8);

        g.hLine(x + 1, x + GUI_W - 2, y + 1, 0x550099CC);
        g.hLine(x + 1, x + GUI_W - 2, y + GUI_H - 2, 0x550099CC);
        g.vLine(x + 1, y + 1, y + GUI_H - 2, 0x550099CC);
        g.vLine(x + GUI_W - 2, y + 1, y + GUI_H - 2, 0x550099CC);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        renderBackground(g, mx, my, partialTick);
        super.render(g, mx, my, partialTick);
        refreshButtonLabels();
        renderTooltip(g, mx, my);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, title, 8, titleLabelY, 0xFF5BC8FF, false);
        g.drawString(font, Component.literal("Event"), 22, 30, 0xFF90CAF9, false);
        g.drawString(font, Component.literal("Action"), GUI_W - 93, 30, 0xFF90CAF9, false);

        for (int i = 0; i < EVENT_NAMES.length; i++) {
            int ty = LIST_Y_OFFSET + i * ROW_H + 5;
            boolean active = menu.isEventActive(i);
            int color = active ? 0xFF80FF80 : 0xFFFFCDD2;
            g.drawString(font, EVENT_NAMES[i], 22, ty, color, false);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
