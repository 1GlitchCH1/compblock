package com.example.compblock.event;

/** Client-side Blood Moon flag. No client-only imports — safe to load on server. */
public final class ClientBloodMoonState {
    private ClientBloodMoonState() {}
    public static volatile boolean active = false;
    public static void set(boolean value) { active = value; }
}
