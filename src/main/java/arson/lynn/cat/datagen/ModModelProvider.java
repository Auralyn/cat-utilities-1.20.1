package arson.lynn.cat.datagen;

import arson.lynn.cat.block.ModBlocks;
import arson.lynn.cat.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // ToDo(): ADD OTHER BLOCKBENCH MODELS
        blockStateModelGenerator.registerAxisRotated(ModBlocks.MILK, new Identifier("block/milk"));
        blockStateModelGenerator.registerAxisRotated(ModBlocks.SCULK_TELEPORTER, new Identifier("item/s"));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.TELEPORTERITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.VELOCITY_STAFF, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OPMILK, new Model(Optional.of(new Identifier("item/opmilk")), Optional.empty()));

    }
}
