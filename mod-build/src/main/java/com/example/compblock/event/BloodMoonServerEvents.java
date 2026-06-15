package com.example.compblock.event;

import com.example.compblock.CompBlockMod;
import com.example.compblock.init.ModGameRules;
import com.example.compblock.network.BloodMoonSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CompBlockMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BloodMoonServerEvents {

    private static final int RESYNC_INTERVAL = 40;
    private static final int SPAWN_INTERVAL  = 100;
    private static final int EXTRA_SPAWNS    = 4;
    private static final int SPAWN_RANGE_MIN = 12;
    private static final int SPAWN_RANGE_MAX = 40;

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        boolean active = sp.server.overworld()
                .getGameRules().getBoolean(ModGameRules.BLOOD_MOON_ACTIVE);
        PacketDistributor.sendToPlayer(sp, new BloodMoonSyncPayload(active));
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;

        boolean active = level.getGameRules().getBoolean(ModGameRules.BLOOD_MOON_ACTIVE);
        long time = level.getGameTime();

        if (time % RESYNC_INTERVAL == 0) {
            for (ServerPlayer player : level.players()) {
                PacketDistributor.sendToPlayer(player, new BloodMoonSyncPayload(active));
            }
        }

        if (active && level.isNight() && time % SPAWN_INTERVAL == 0) {
            for (ServerPlayer player : level.players()) {
                for (int i = 0; i < EXTRA_SPAWNS; i++) trySpawnExtraMob(level, player);
            }
        }
    }

    private static void trySpawnExtraMob(ServerLevel level, ServerPlayer player) {
        double angle = level.random.nextDouble() * Math.PI * 2;
        int range = SPAWN_RANGE_MIN + level.random.nextInt(SPAWN_RANGE_MAX - SPAWN_RANGE_MIN);
        int x = (int)(player.getX() + Math.cos(angle) * range);
        int z = (int)(player.getZ() + Math.sin(angle) * range);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (level.getBrightness(LightLayer.BLOCK, pos) > 7) return;

        EntityType<?> type = switch (level.random.nextInt(4)) {
            case 0  -> EntityType.ZOMBIE;
            case 1  -> EntityType.SKELETON;
            case 2  -> EntityType.SPIDER;
            default -> EntityType.CREEPER;
        };
        var entity = type.create(level);
        if (entity == null) return;
        entity.moveTo(x + 0.5, y, z + 0.5, level.random.nextFloat() * 360f, 0f);
        if (entity instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
            if (mob instanceof Zombie z2) z2.setCanBreakDoors(true);
        }
        level.addFreshEntityWithPassengers(entity);
    }
}
