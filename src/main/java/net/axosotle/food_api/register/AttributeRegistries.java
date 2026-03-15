package net.axosotle.food_api.register;

import net.axosotle.food_api.FoodApi;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = FoodApi.MODID)
public class AttributeRegistries {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, FoodApi.MODID);


    public static final Holder<Attribute> MAX_HUNGER = ATTRIBUTES.register("max_hunger",
            () -> new RangedAttribute(ResourceLocation.fromNamespaceAndPath(FoodApi.MODID, "max_hunger").toString(), 20, 1, 40).setSyncable(true));

    public static final Holder<Attribute> HUNGER_RATE  = ATTRIBUTES.register("hunger_rate",
            () -> new RangedAttribute(ResourceLocation.fromNamespaceAndPath(FoodApi.MODID, "hunger_rate").toString(), 1, 0, 10).setSyncable(true));


    public static void register(IEventBus eventBus){
        ATTRIBUTES.register(eventBus);
    }

    @SubscribeEvent
    private static void attachToPlayer(EntityAttributeModificationEvent e) {
        for (DeferredHolder<Attribute, ? extends Attribute> entry : ATTRIBUTES.getEntries()) {
            e.add(EntityType.PLAYER, entry.getDelegate());
        }
    }
}
