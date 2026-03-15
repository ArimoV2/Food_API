package net.axosotle.food_api.mixin;

import net.axosotle.food_api.register.FoodIconRegistry;
import net.axosotle.food_api.util.FoodIconProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public class MixinHUDOverlayHandler {

    @Redirect(
            method = "drawSaturationOverlay(FFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/gui/GuiGraphics;IIFI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    private static void redirectSaturationBlit(
            GuiGraphics guiGraphics,
            ResourceLocation original,
            int x, int y, int u, int v,
            int width, int height,
            float saturation, float exhaustion, Player player,
            GuiGraphics g2, int px, int py, float f, int i
    ) {
        ResourceLocation texture = FoodIconRegistry.resolve(
                player,
                0,
                0,
                FoodIconProvider.FoodIconType.SATURATION
        );

        guiGraphics.blit(texture, x, y, u, v, width, height);
    }
}
