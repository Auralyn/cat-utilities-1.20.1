package arson.lynn.cat.item.custom;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;

import java.math.BigDecimal;
import java.util.Random;

public class VelocityStaffItem extends Item {
    public VelocityStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW; // Change the action to drinking
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        ItemStack itemStack = user.getStackInHand(hand);

        BlockPos frontOfPlayer = user.getBlockPos().offset(user.getHorizontalFacing(), 10);
        world.playSoundFromEntity(null, user, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1.0F, 1.0F);
        float yaw = user.getYaw(); // Get player's yaw
        float pitch = user.getPitch(); // Get player's pitch

        double rad_yaw = Math.toRadians(yaw); // Get player's yaw
        double rad_pitch = Math.toRadians(pitch); // Get player's pitch

// Calculate forward vector based on yaw and pitch
        Vec3d forward = new Vec3d(
                -Math.sin(rad_yaw) * Math.cos(rad_pitch),
                -Math.sin(rad_pitch),
                Math.cos(rad_yaw) * Math.cos(rad_pitch)
        ).normalize();

        double launchVelocity = 2.5;
        switch (EnchantmentHelper.getLevel(ModEnchantments.MAGICAL_POWER, itemStack)) {
            case 1 -> launchVelocity *= 1.25;
            case 2 -> launchVelocity *= 1.50;
            case 3 -> launchVelocity *= 2;
        }

// Apply the for
//        user.sendMessage(Text.literal(
//                "y:" + yaw +
//                        "\n p:" + pitch +
//                        "\n f_x:" + forward.getX() +
//                        "\n f_y" + forward.getY() +
//                        "\n f_z" + forward.getZ() +
//                        "\n fm_x" + forward.multiply(launchVelocity).x +
//                        "\n fm_y" + forward.multiply(launchVelocity).y +
//                        "\n fm_z" + forward.multiply(launchVelocity).z ));
        user.setVelocity(forward.multiply(launchVelocity));

// Ensure velocity is applied
        user.velocityModified = true;

        // Spawn the TNT entity in the world

        // Rotate the vector around the Y-axis for the next point

        itemStack.damage(1, user, (entity) -> entity.sendToolBreakStatus(hand));

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
