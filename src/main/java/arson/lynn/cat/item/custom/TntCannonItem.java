package arson.lynn.cat.item.custom;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.joml.Matrix3f;

public class TntCannonItem extends BlockItem {
    public TntCannonItem(Block block, Settings settings) {
        super(block, settings);
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

        ItemStack tntStack = new ItemStack(Items.TNT); // Create an ItemStack for TNT

        int tntCount = user.getInventory().count(tntStack.getItem());

        if(tntCount > 0 || user.isCreative()) {
            user.setCurrentHand(hand);

            tntStack.decrement(1);
            user.getInventory().removeOne(tntStack);
            BlockPos frontOfPlayer = user.getBlockPos().offset(user.getHorizontalFacing(), 10);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            // Spawn the lightning bolt.
            int n = 12;
            double radius = 10f;
            Vec3d vector = new Vec3d(radius, 0f, 0f);
            Vec3d center = new Vec3d(user.getX(), user.getY(), user.getZ());
            float angle = (float) Math.PI * 2 / (float) n;
            Vec3d[] points = new Vec3d[n];
            Random random = new Random();
            Matrix3f rotation = new Matrix3f().rotateZ(angle);
            for (int i = 0; i < n; i++) {
                // Get the player's position
                Vec3d playerPosition = new Vec3d(user.getX(), user.getY(), user.getZ());
                // Calculate the direction vector from the player's look direction
                float yaw = user.getYaw(); // Get player's yaw
                float pitch = user.getPitch(); // Get player's pitch

                float yawOffset = (random.nextFloat() - 0.5f) * 20; // Random offset between -10 and 10 degrees
                float pitchOffset = (random.nextFloat() - 0.5f) * 20;

                yaw += yawOffset;
                pitch += pitchOffset;

                // Calculate forward vector based on yaw and pitch
                Vec3d forward = new Vec3d(
                        -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                        -Math.sin(Math.toRadians(pitch)),
                        Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
                ).normalize(); // Normalize to unit length

                // Create a target point at a distance along the forward vector
                double distance = 22 * 4; // Adjust the distance as needed
                Vec3d targetPoint = playerPosition.add(forward.multiply(distance)); // Get the target point

                // Create a new TNT entity
                TntEntity tnt = new TntEntity(EntityType.TNT, world);
                tnt.setFuse(80); // Set a fuse time for the TNT
                tnt.updatePosition(playerPosition.x, playerPosition.y, playerPosition.z); // Start at the player's position
                // Set the TNT's velocity to launch it towards the target point
                double launchVelocity = 3.5; // Adjust this value to change the launch speed
                tnt.setVelocity(forward.multiply(launchVelocity).add(0, 0.5, 0)); // Add a slight upward velocity

                // Spawn the TNT entity in the world
                world.spawnEntity(tnt);

                // Rotate the vector around the Y-axis for the next point
                vector = rotateAroundY(vector, angle);
            }

        }

        itemStack.damage(1, user, (entity) -> entity.sendToolBreakStatus(hand));

        return TypedActionResult.success(user.getStackInHand(hand));
    }
    public Vec3d rotateAroundY(Vec3d vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Apply the rotation matrix for Y-axis:
        // [ cosθ  0  sinθ ]
        // [  0    1   0   ]
        // [ -sinθ  0  cosθ ]
        double newX = vector.x * cos - vector.z * sin;
        double newZ = vector.x * sin + vector.z * cos;

        // Return the new rotated vector
        return new Vec3d(newX, vector.y, newZ);
    }
}
