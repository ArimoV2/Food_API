package net.axosotle.food_api.compat;

import fuzs.overflowingbars.client.gui.RowCountRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class OverflowingBarsCompatImpl {

    public static void drawRowCount(GuiGraphics guiGraphics, int x, int y, int currentFood) {
        RowCountRenderer.drawBarRowCount(
                guiGraphics, x, y, currentFood, true, 20,
                Minecraft.getInstance().font
        );
    }
}
