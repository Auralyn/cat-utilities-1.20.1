package arson.lynn.cat.item;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.block.custom.MilkBlock;
import arson.lynn.cat.item.custom.MilkBlockItem;
import arson.lynn.cat.item.custom.TntCannonItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ModItems {
    public static final Item TNTCANNON = registerItem("tnt_cannon", new TntCannonItem(new FabricItemSettings().maxDamage(385 * 2)));
    public static final Item REDBALL = registerItem("redball", new Item(new FabricItemSettings()));
    /*public static final Item MILK = registerItem("milk", new Item(new FabricItemSettings().rarity(Rarity.EPIC).food(
            new FoodComponent.Builder()
                    .alwaysEdible()
                    .hunger(2)
                    .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 20, 1), 0.1f)
                    .build()
    ))  {
        @Override
        public int getMaxUseTime(ItemStack stack) {
            return 32; // Modify this value to change the eating duration
        }
        @Override
        public SoundEvent getDrinkSound() {
            return SoundEvents.ENTITY_GENERIC_DRINK; // Modify this value to change the eating duration
        }
        @Override
        public SoundEvent getEatSound() {
            return SoundEvents.ENTITY_GENERIC_DRINK; // Use the same drink sound for eating
        }
        @Override
        public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
            if (user instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) user;

                player.clearStatusEffects();
                player.extinguish();
            }
            return super.finishUsing(stack, world, user);
        }

        @Override
        public UseAction getUseAction(ItemStack stack) {
            return UseAction.DRINK; // Change the action to drinking
        }
    });*/

    public static final Item OPMILK = registerItem("opmilk", new Item(new FabricItemSettings().rarity(Rarity.EPIC).food(
            new FoodComponent.Builder()
                    .alwaysEdible()
                    .hunger(20)
                    .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 20, 1), 0.01f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 60 * 20, 2), 1.0f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 120 * 20, 3), 1.0f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 240 * 20, 2), 1.0f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 500 * 20, 4), 1.0f)
                    .build()
    ))  {
        @Override
        public int getMaxUseTime(ItemStack stack) {
            return 64; // Modify this value to change the eating duration
        }
        public boolean hasEffect(ItemStack stack) {
            return true; // Modify this value to change the eating duration
        }
        @Override
        public SoundEvent getDrinkSound() {
            return SoundEvents.ENTITY_GENERIC_DRINK; // Modify this value to change the eating duration
        }
        @Override
        public SoundEvent getEatSound() {
            return SoundEvents.ENTITY_GENERIC_DRINK; // Use the same drink sound for eating
        }
        @Override
        public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
            if (user instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) user;

                player.clearStatusEffects();
                player.extinguish();
            }
            return super.finishUsing(stack, world, user);
        }

        @Override
        public UseAction getUseAction(ItemStack stack) {
            return UseAction.DRINK; // Change the action to drinking
        }
    });
    public static final Item SENTIENCE = registerItem("sentience", new Item(new FabricItemSettings()));

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(TNTCANNON);
        entries.add(REDBALL);
        entries.add(SENTIENCE);
        entries.add(OPMILK);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CatUtilities.MOD_ID, name), item);
    }
    public static void registerModItems() {
        CatUtilities.LOGGER.info("Registering Mod Items for" + CatUtilities.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
