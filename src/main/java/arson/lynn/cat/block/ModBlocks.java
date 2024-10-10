package arson.lynn.cat.block;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.block.custom.MilkBlock;
import arson.lynn.cat.block.custom.Potion_TerracottaBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block MILK = registerBlock("milk",
            new MilkBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).sounds(BlockSoundGroup.WOOL).nonOpaque()));

    public static final Block POTION_TERRACOTTA = registerBlock("potion_terracotta",
            new Potion_TerracottaBlock(FabricBlockSettings.copyOf(Blocks.CYAN_GLAZED_TERRACOTTA).sounds(BlockSoundGroup.NETHER_WOOD)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CatUtilities.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CatUtilities.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerModBlocks() {
        CatUtilities.LOGGER.info("Registering Mod Blocks for " + CatUtilities.MOD_ID);
    }
}
