package arson.lynn.cat.datagen;

import arson.lynn.cat.block.ModBlocks;
import arson.lynn.cat.block.custom.MilkBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.CandleBlock;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

import java.util.List;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    protected ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        candleDrops(ModBlocks.MILK);
    }

    public LootTable.Builder candleDrops(Block milk) {
        return LootTable.builder()
                .pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0F))
                                .with((LootPoolEntry.Builder<?>)
                                        this.applyExplosionDecay(milk, ItemEntry.builder(milk)
                                                .apply(List.of(2, 3, 4, 5, 6),
                                                        milk_amount -> SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)
                                                                milk_amount.intValue()))
                                                                .conditionally(BlockStatePropertyLootCondition.builder(milk)
                                                                        .properties(StatePredicate.Builder.create().exactMatch(MilkBlock.MILKS, milk_amount)))))));
    }
}
