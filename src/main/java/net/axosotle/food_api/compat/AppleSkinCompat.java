package net.axosotle.food_api.compat;

import net.neoforged.fml.ModList;

public final class AppleSkinCompat {

    public static final boolean LOADED = ModList.get().isLoaded("appleskin");

    private AppleSkinCompat() {}

    public static Object getSaturationTexture() {
        if (!LOADED) return null;
        return AppleSkinCompatImpl.getSaturationTexture();
    }
}
