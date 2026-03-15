package net.axosotle.food_api.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface FoodIconProvider {


    @Nullable
    ResourceLocation getSprite(Player player, int iconIndex, int row, FoodIconType type);

    enum FoodIconType {
        EMPTY,
        HALF,
        FULL,
        EMPTY_HUNGER,
        HALF_HUNGER,
        FULL_HUNGER,
        HALF_OVERLAY,
        FULL_OVERLAY,
        SATURATION
    }
}
