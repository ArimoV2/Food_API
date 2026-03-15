package net.axosotle.food_api.mixin;

import net.axosotle.food_api.register.AttributeRegistries;
import net.axosotle.food_api.util.IFoodData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodData.class)
public class FoodDataMixin implements IFoodData {

    @Shadow private int foodLevel;

    @Shadow private float saturationLevel;

    private Player food_api$player;

    @Override
    public void food_api$setPlayer(Player player) {
        this.food_api$player = player;
    }

    @Override
    public Player food_api$getPlayer() {
        return this.food_api$player;
    }

    private int food_api$getMaxFoodLevel() {
        if (food_api$player == null) return 20;
        AttributeInstance attr = food_api$player.getAttribute(AttributeRegistries.MAX_HUNGER);
        return attr == null ? 20 : Mth.clamp((int) attr.getValue(), 1, 40);
    }

    private float food_api$getExhaustionThreshold() {
        if (food_api$player == null) return 4.0F;
        AttributeInstance attr = food_api$player.getAttribute(AttributeRegistries.HUNGER_RATE);
        if (attr == null) return 4.0F;
        double speed = attr.getValue();
        return speed <= 0.0 ? 41.0F : (float) (4.0 / speed);
    }

    @ModifyConstant(method = "add", constant = @Constant(intValue = 20))
    private int food_api$maxFoodInAdd(int original) {
        return food_api$getMaxFoodLevel();
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 4.0F, ordinal = 0))
    private float food_api$exhaustionCheck(float original) {
        return food_api$getExhaustionThreshold();
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 4.0F, ordinal = 1))
    private float food_api$exhaustionSubtract(float original) {
        return food_api$getExhaustionThreshold();
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    private int food_api$fastRegenThreshold(int original) {
        return food_api$getMaxFoodLevel();
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 18))
    private int food_api$slowRegenThreshold(int original) {
        return food_api$getMaxFoodLevel() - 2;
    }

    @Inject(method = "needsFood", at = @At("HEAD"), cancellable = true)
    private void food_api$needsFood(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.foodLevel < food_api$getMaxFoodLevel());
    }

    @Inject(method = "add", at = @At("TAIL"))
    private void food_api$capSaturation(int foodLevel, float saturationLevel, CallbackInfo ci) {
        this.saturationLevel = Math.min(this.saturationLevel, 20.0F);
    }
}