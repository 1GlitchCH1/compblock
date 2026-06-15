package com.example.compblock.init;

import net.minecraft.world.level.GameRules;

/**
 * Custom game rules for CompBlock mod.
 * Vanilla automatically syncs game rules to every connected client —
 * no custom networking needed.
 */
public final class ModGameRules {

    /** True while the Blood Moon event is active. Synced to clients by Minecraft. */
    public static final GameRules.Key<GameRules.BooleanValue> BLOOD_MOON_ACTIVE =
            GameRules.register(
                    "compblock_bloodMoon",
                    GameRules.Category.MISC,
                    GameRules.BooleanValue.create(false)
            );

    private ModGameRules() {}

    /**
     * Call once during mod init to force static field initialisation
     * (and therefore registration) before any world loads.
     */
    public static void init() {}
}
