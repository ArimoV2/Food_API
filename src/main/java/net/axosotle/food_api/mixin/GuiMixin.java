package net.axosotle.food_api.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import net.axosotle.food_api.FoodApi;
import net.axosotle.food_api.compat.OverflowingBarsCompat;
import net.axosotle.food_api.register.AttributeRegistries;
import net.axosotle.food_api.register.FoodIconRegistry;
import net.axosotle.food_api.util.FoodIconProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    public int rightHeight;


    @Inject(method = "renderFoodLevel", at = @At("TAIL"))
    private void food_api$adjustRightHeightAfter(GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = food_api$getCameraPlayer(guiGraphics);
        if (player == null) return;

        if (!FoodApi.OB_LOADED && food_api$getMaxFood(player) > 20) {
            this.rightHeight += 10;
        }
    }

    @SuppressWarnings("all")
    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void food_api$renderFood(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        int maxFood = food_api$getMaxFood(player);
        boolean isHunger = player.hasEffect(MobEffects.HUNGER);

        boolean hasCustomProviders = !FoodIconRegistry.isEmpty();
        if (maxFood == 20 && !hasCustomProviders) return;

        ci.cancel();

        FoodData foodData = player.getFoodData();
        int currentFood = foodData.getFoodLevel();
        int totalIcons = (maxFood + 1) / 2;

        int guiTicks = ((Gui)(Object)this).getGuiTicks();
        boolean shouldShake = foodData.getSaturationLevel() <= 0.0F
                && guiTicks % (currentFood * 3 + 1) == 0;

        int[] colShake = new int[10];
        if (shouldShake) {
            if (FoodApi.OB_LOADED) {
                RandomSource rand = RandomSource.create(guiTicks);
                for (int col = 0; col < 10; col++) {
                    colShake[col] = rand.nextInt(3) - 1;
                }
            } else {
                RandomSource rand = RandomSource.create(guiTicks);
                for (int col = 0; col < 10; col++) {
                    colShake[col] = rand.nextInt(3) - 1;
                }
            }
        }

        RenderSystem.enableBlend();

        for (int icon = 0; icon < totalIcons; ++icon) {
            int row = icon / 10;
            int col = icon % 10;

            boolean isOverlay = FoodApi.OB_LOADED && row == 1;
            int renderY = isOverlay ? y : y - row * 10;
            int renderX = x - col * 8 - 9;
            int drawY = renderY + colShake[col];

            int foodValue = icon * 2;

            if (isOverlay) {
                if (foodValue + 1 < currentFood) {
                    guiGraphics.blitSprite(
                            FoodIconRegistry.resolve(player, col, row, FoodIconProvider.FoodIconType.FULL_OVERLAY),
                            renderX, drawY, 9, 9);
                } else if (foodValue + 1 == currentFood) {
                    guiGraphics.blitSprite(
                            FoodIconRegistry.resolve(player, col, row, FoodIconProvider.FoodIconType.HALF_OVERLAY),
                            renderX, drawY, 9, 9);
                }
            } else {
                FoodIconProvider.FoodIconType emptyType = isHunger
                        ? FoodIconProvider.FoodIconType.EMPTY_HUNGER
                        : FoodIconProvider.FoodIconType.EMPTY;
                FoodIconProvider.FoodIconType halfType = isHunger
                        ? FoodIconProvider.FoodIconType.HALF_HUNGER
                        : FoodIconProvider.FoodIconType.HALF;
                FoodIconProvider.FoodIconType fullType = isHunger
                        ? FoodIconProvider.FoodIconType.FULL_HUNGER
                        : FoodIconProvider.FoodIconType.FULL;

                guiGraphics.blitSprite(
                        FoodIconRegistry.resolve(player, col, row, emptyType),
                        renderX, drawY, 9, 9);

                if (foodValue + 1 < currentFood) {
                    guiGraphics.blitSprite(
                            FoodIconRegistry.resolve(player, col, row, fullType),
                            renderX, drawY, 9, 9);
                } else if (foodValue + 1 == currentFood) {
                    guiGraphics.blitSprite(
                            FoodIconRegistry.resolve(player, col, row, halfType),
                            renderX, drawY, 9, 9);
                }
            }


        }

        if (FoodApi.OB_LOADED) {
            OverflowingBarsCompat.drawRowCount(
                    guiGraphics,
                    x + 10,
                    y,
                    currentFood
            );
        }

        RenderSystem.disableBlend();
    }


    private int food_api$getMaxFood(Player player) {
        AttributeInstance attr = player.getAttribute(AttributeRegistries.MAX_HUNGER);
        return attr == null ? 20 : Mth.clamp((int) attr.getValue(), 1, 40);
    }

    private Player food_api$getCameraPlayer(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Entity cam = mc.getCameraEntity();
        return cam instanceof Player p ? p : null;
    }
}
