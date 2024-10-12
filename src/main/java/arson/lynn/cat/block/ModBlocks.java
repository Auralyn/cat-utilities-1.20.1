package arson.lynn.cat.block;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.block.custom.MilkBlock;
import arson.lynn.cat.block.custom.Potion_TerracottaBlock;
import arson.lynn.cat.item.custom.MilkBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModBlocks {

    public static final Block MILK = registerMilkBlock("milk",
            new MilkBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).sounds(BlockSoundGroup.WOOL).nonOpaque()));

    public static final Block POTION_TERRACOTTA = registerBlock("potion_terracotta",
            new Potion_TerracottaBlock(FabricBlockSettings.copyOf(Blocks.SCULK_SHRIEKER).sounds(BlockSoundGroup.SCULK_SHRIEKER)));

    public static final Block SCULK_TELEPORTER = registerBlock("sculk_teleporter.json",
            new Block(FabricBlockSettings
                    .copyOf(Blocks.SCULK_SHRIEKER)
                    .sounds(BlockSoundGroup.SCULK_SHRIEKER)
                    .mapColor(MapColor.RED)
                    .requiresTool()
                    .strength(3.5F)
                    .pistonBehavior(PistonBehavior.BLOCK)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CatUtilities.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CatUtilities.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CatUtilities.MOD_ID, name), item);
    }

    private static Block registerMilkBlock(String name, Block block) {
        registerMilkBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CatUtilities.MOD_ID, name), block);
    }
    private static Item registerMilkBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CatUtilities.MOD_ID, name),
                new MilkBlockItem(block, new FabricItemSettings().rarity(Rarity.EPIC).food(
                        new FoodComponent.Builder()
                                .alwaysEdible()
                                .build()
                )));
    }

    //ToDO: CLEAN UP
    public static void registerModBlocks() {
        CatUtilities.LOGGER.info("Registering Mod Blocks for " + CatUtilities.MOD_ID);
    }
}
