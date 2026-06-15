package com.example.compblock;

import com.example.compblock.client.CompBlockEntityRenderer;
import com.example.compblock.event.ClientBloodMoonState;
import com.example.compblock.init.ModBlockEntityTypes;
import com.example.compblock.init.ModBlocks;
import com.example.compblock.init.ModCreativeTabs;
import com.example.compblock.init.ModGameRules;
import com.example.compblock.init.ModItems;
import com.example.compblock.init.ModMenuTypes;
import com.example.compblock.network.BloodMoonSyncPayload;
import com.example.compblock.screen.EventListScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(CompBlockMod.MODID)
public class CompBlockMod {

    public static final String MODID = "compblock";

    public CompBlockMod(IEventBus modEventBus) {
        ModGameRules.init();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerRenderers);
        // BloodMoonColorHandlers is auto-discovered via @EventBusSubscriber
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToClient(
                BloodMoonSyncPayload.TYPE,
                BloodMoonSyncPayload.STREAM_CODEC,
                (payload, ctx) -> ClientBloodMoonState.set(payload.active())
        );
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.EVENT_LIST_MENU.get(), EventListScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntityTypes.COMP_BLOCK_ENTITY.get(),
                CompBlockEntityRenderer::new);
    }
}
