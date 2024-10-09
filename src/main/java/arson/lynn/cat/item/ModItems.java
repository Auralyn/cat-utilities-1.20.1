package arson.lynn.cat.item;

import arson.lynn.cat.CatUtilities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item ORACLE = registerItem("oracle", new Item(new FabricItemSettings()));
    public static final Item REDBALL = registerItem("redball", new Item(new FabricItemSettings()));
    public static final Item MILK = registerItem("milk", new Item(new FabricItemSettings().rarity(Rarity.EPIC).food(
            new FoodComponent.Builder()
                    .alwaysEdible()
                    .hunger(20)
                    .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 20, 1), 0.1f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120 * 20, 2), 1.0f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 120 * 20, 1), 1.0f)
                    .build()
    ))  {
        @Override
        public int getMaxUseTime(ItemStack stack) {
            return 136; // Modify this value to change the eating duration
        }
    });
    public static final Item SENTIENCE = registerItem("sentience", new Item(new FabricItemSettings().food(
            new FoodComponent.Builder()
                    .alwaysEdible()
                    .snack()
                    .hunger(-5)
                    .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 120 * 20, 1), 1.0f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 2 * 20, 1), 1.0f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 120 * 20, 1), 1.0f)
                    .build()
    )));


    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(ORACLE);
        entries.add(REDBALL);
        entries.add(SENTIENCE);
        entries.add(MILK);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CatUtilities.MOD_ID, name), item);
    }
    public static void registerModItems() {
        CatUtilities.LOGGER.info("Registering Mod Items for" + CatUtilities.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
