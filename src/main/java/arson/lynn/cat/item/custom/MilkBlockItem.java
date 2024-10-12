package arson.lynn.cat.item.custom;

import java.util.Random;

import arson.lynn.cat.CatUtilities;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;

public class MilkBlockItem extends BlockItem {
    public MilkBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32; // Modify this value to change the eating duration
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
}
