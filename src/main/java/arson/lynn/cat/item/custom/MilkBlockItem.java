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
        // Check if the player is sneaking (holding Shift)
        if (player.isSneaking()) {
            // If sneaking, allow placing the block by calling the default place behavior
            return super.use(world, player, hand); // This calls the normal block placement logic
        } else {
            // If not sneaking, do something else (custom use logic)
            if (!world.isClient) {
                player.sendMessage(Text.of("You used the milk block in your hand!"), true);

                // You can add any other custom behavior here
            }
            return TypedActionResult.success(player.getStackInHand(hand));
        }
    }
}
