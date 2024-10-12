package arson.lynn.cat.item.custom;

import arson.lynn.cat.block.ModBlocks;
import com.mojang.logging.LogUtils;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.swing.*;

public class TeleporterItem extends Item implements Vanishable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SHRIEKER_POS_KEY = "ShriekerPos";

    public TeleporterItem(Item.Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if (!world.isClient) {
            ItemStack itemStack = context.getPlayer().getStackInHand(context.getHand());

            if (world.getBlockState(blockPos).isOf(Blocks.SCULK_SHRIEKER) || world.getBlockState(blockPos).isOf(ModBlocks.SCULK_TELEPORTER)) {
                // Store block position in NBT
                NbtCompound nbt = itemStack.getOrCreateNbt();
                nbt.put(SHRIEKER_POS_KEY, NbtHelper.fromBlockPos(blockPos));

                world.playSound(null, context.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Optionally send feedback to player
                context.getPlayer().sendMessage(Text.literal("Block position stored: " + blockPos), true);
            } else {
                if (context.getPlayer().isSneaking()) {
                    return use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
                } else {
                    return super.useOnBlock(context);
                }

            }

        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.contains(SHRIEKER_POS_KEY);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound nbt = itemStack.getOrCreateNbt();

        // Check if teleport is already in progress
        if (!nbt.contains("teleport_timer")) {
            // Start teleport countdown (40 ticks = 2 seconds)
            nbt.putInt("teleport_timer", 40);

            user.sendMessage(Text.literal("Teleport starting..."), false);
            world.playSoundFromEntity(null, user, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.PLAYERS, 1.0F, 0.5F);
            world.playSound(null, NbtHelper.toBlockPos(nbt.getCompound(SHRIEKER_POS_KEY)), SoundEvents.ENTITY_WARDEN_EMERGE, SoundCategory.BLOCKS, 1.0F, 1F);
            PlayerEntity player = (PlayerEntity) user;
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 10));


            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }

    // Call this method each tick to handle the teleport countdown
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            NbtCompound nbt = stack.getOrCreateNbt();

            if (nbt.contains("teleport_timer")) {
                int timer = nbt.getInt("teleport_timer");

                world.addParticle(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 0, 0.5, 0);

                // Countdown the timer
                if (timer >= 0) {
                    // Play particles and sound during countdown

                    player.sendMessage(Text.literal("if" + String.valueOf(timer)), true);

                    // Decrement the timer
                    nbt.putInt("teleport_timer", timer - 1);
                } else {

                    player.sendMessage(Text.literal("else" + String.valueOf(timer)), true);

                    // Timer is complete, teleport the player
                    BlockPos storedPos = NbtHelper.toBlockPos(nbt.getCompound(SHRIEKER_POS_KEY));
                    player.sendMessage(Text.literal("Teleporting to stored position..."), true);

                    player.teleport(storedPos.getX() + 0.5, storedPos.getY() + 1, storedPos.getZ() + 0.5);

                    // Reset the timer by removing it from NBT
                    nbt.remove("teleport_timer");

                    // Add a cooldown if the player is not in creative mode
                    if (!player.isCreative()) {
                        player.getItemCooldownManager().set(this, 100);
                    }
                }
            }
        }
    }

}
