package net.axosotle.food_api.register;

import net.axosotle.food_api.FoodApi;
import net.axosotle.food_api.compat.AppleSkinCompat;
import net.axosotle.food_api.util.FoodIconProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FoodIconRegistry {

    private static final List<Entry> PROVIDERS = new ArrayList<>();

    private FoodIconRegistry() {}


    public static void register(ResourceLocation id, int priority, FoodIconProvider provider) {
        PROVIDERS.removeIf(e -> e.id().equals(id));
        PROVIDERS.add(new Entry(id, priority, provider));
        PROVIDERS.sort(Comparator.comparingInt(Entry::priority).reversed());
    }

    public static boolean isEmpty() {
        return PROVIDERS.isEmpty();
    }

    public static ResourceLocation resolve(
            Player player,
            int iconIndex,
            int row,
            FoodIconProvider.FoodIconType type
    ) {
        for (Entry entry : PROVIDERS) {
            ResourceLocation sprite = entry.provider().getSprite(player, iconIndex, row, type);
            if (sprite != null) return sprite;
        }
        return getVanillaSprite(type);
    }

    public static ResourceLocation getVanillaSprite(FoodIconProvider.FoodIconType type) {
        return switch (type) {
            case EMPTY        -> ResourceLocation.withDefaultNamespace("hud/food_empty");
            case HALF         -> ResourceLocation.withDefaultNamespace("hud/food_half");
            case FULL         -> ResourceLocation.withDefaultNamespace("hud/food_full");
            case EMPTY_HUNGER -> ResourceLocation.withDefaultNamespace("hud/food_empty_hunger");
            case HALF_HUNGER  -> ResourceLocation.withDefaultNamespace("hud/food_half_hunger");
            case FULL_HUNGER  -> ResourceLocation.withDefaultNamespace("hud/food_full_hunger");
            case HALF_OVERLAY -> FoodApi.prefix("hud/food_half");
            case FULL_OVERLAY -> FoodApi.prefix("hud/food_full");
            case SATURATION -> {
                if (AppleSkinCompat.LOADED) {
                    yield (ResourceLocation) AppleSkinCompat.getSaturationTexture();
                }
                yield ResourceLocation.withDefaultNamespace("hud/food_empty");
            }
        };
    }

    private record Entry(ResourceLocation id, int priority, FoodIconProvider provider) {}
}
