package arson.lynn.cat.item;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup CAT_UTILITIES = Registry.register(Registries.ITEM_GROUP,
            new Identifier(CatUtilities.MOD_ID, "cat-utilities"),
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.cat-utilities"))
                    .icon(() -> new ItemStack(ModBlocks.MILK)).entries((displayContext, entries) -> {
                        entries.add(ModItems.OPMILK);
                        entries.add(ModItems.VELOCITY_STAFF);
                        entries.add(ModItems.TELEPORTERITEM);

                        entries.add(ModBlocks.MILK);
                        //entries.add(ModBlocks.PANCAKE);
                        entries.add(ModBlocks.POTION_TERRACOTTA);
                        entries.add(ModBlocks.SCULK_TELEPORTER);
                    }).build());

    public static void registerItemGroups() {
        CatUtilities.LOGGER.info("Registering Item Groups for" + CatUtilities.MOD_ID);
    }
}
