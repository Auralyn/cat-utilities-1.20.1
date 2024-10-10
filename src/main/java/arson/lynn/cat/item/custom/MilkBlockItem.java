package arson.lynn.cat.item.custom;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MilkBlockItem extends BlockItem {
    public MilkBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Message and consumption behavior
        if (!world.isClient) {
            player.sendMessage(Text.literal("You drank the milk block!"), false);

            // Here you can also apply effects or additional logic
            player.heal(4.0F);  // Example: heal the player when used

            // Consume one item
            itemStack.decrement(1);
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
    }
}
