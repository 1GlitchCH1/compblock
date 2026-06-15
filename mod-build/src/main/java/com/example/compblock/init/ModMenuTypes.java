package com.example.compblock.init;

import com.example.compblock.CompBlockMod;
import com.example.compblock.menu.EventListMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {

    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, CompBlockMod.MODID);

    public static final Supplier<MenuType<EventListMenu>> EVENT_LIST_MENU =
            MENU_TYPES.register("event_list_menu", () ->
                    IMenuTypeExtension.create(EventListMenu::new));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
