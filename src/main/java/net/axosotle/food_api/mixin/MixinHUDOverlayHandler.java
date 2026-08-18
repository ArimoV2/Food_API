package net.axosotle.food_api.mixin;

import net.axosotle.food_api.register.AttributeRegistries;
import net.axosotle.food_api.register.FoodIconRegistry;
import net.axosotle.food_api.util.FoodIconProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public class MixinHUDOverlayHandler {

    private static int food_api$maxFood(Player player) {
        if (player == null) {
            return 20;
        }
        AttributeInstance attr = player.getAttribute(AttributeRegistries.MAX_HUNGER);
        return attr == null ? 20 : Mth.clamp((int) attr.getValue(), 1, 40);
    }

    @ModifyConstant(
            method = "drawSaturationOverlay(FFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/gui/GuiGraphics;IIFI)V",
            constant = @Constant(floatValue = 20.0F)
    )
    private static float food_api$maxSaturationOverlay(
            float original,
            float saturationGained,
            float saturationLevel,
            Player player
    ) {
        return food_api$maxFood(player);
    }

    @ModifyConstant(
            method = "drawHungerOverlay(IILnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/gui/GuiGraphics;IIFZI)V",
            constant = @Constant(intValue = 20)
    )
    private static int food_api$maxHungerOverlay(
            int original,
            int hungerRestored,
            int foodLevel,
            Player player
    ) {
        return food_api$maxFood(player);
    }

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
