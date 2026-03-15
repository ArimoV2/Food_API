package net.axosotle.food_api.compat;


import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.fml.ModList;

public final class OverflowingBarsCompat {

    public static final boolean LOADED = ModList.get().isLoaded("overflowingbars");

    private OverflowingBarsCompat() {}

    public static void drawRowCount(GuiGraphics guiGraphics, int x, int y, int currentFood) {
        if (!LOADED) return;
        OverflowingBarsCompatImpl.drawRowCount(guiGraphics, x, y, currentFood);
    }
}
