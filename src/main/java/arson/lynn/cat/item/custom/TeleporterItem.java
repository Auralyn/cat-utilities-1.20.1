package arson.lynn.cat.item.custom;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.block.ModBlocks;
import arson.lynn.cat.util.ModTags;
import com.mojang.logging.LogUtils;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
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
    public static final String SHRIEKER_DIMENSION_KEY = "ShriekerDimension";
    public static final String SHRIEKER_TRACKED_KEY = "ShriekerTracked";
    public static final String SHRIEKER_POS_KEY = "ShriekerPos";

    public TeleporterItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean hasLodestone(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && (nbtCompound.contains(SHRIEKER_DIMENSION_KEY) || nbtCompound.contains(SHRIEKER_POS_KEY));
    }

    private static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get(SHRIEKER_DIMENSION_KEY)).result();
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.cat-utilities.shrieker_teleporter.tooltip"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    private void writeNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt) {
        nbt.put(SHRIEKER_POS_KEY, NbtHelper.fromBlockPos(pos));
        World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbt.put(SHRIEKER_DIMENSION_KEY, nbtElement));
        nbt.putBoolean(SHRIEKER_TRACKED_KEY, true);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if (!world.isClient) {
            ItemStack itemStack = context.getPlayer().getStackInHand(context.getHand());

            if (isAssignableBlock(world.getBlockState(blockPos))) {
                context.getPlayer().playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                // Store block position in NBT
                PlayerEntity playerEntity = context.getPlayer();
                NbtCompound nbt = itemStack.getOrCreateNbt();
                //nbt.put(SHRIEKER_POS_KEY, NbtHelper.fromBlockPos(blockPos));


                this.writeNbt(world.getRegistryKey(), blockPos, itemStack.getOrCreateNbt());
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

    public boolean isAssignableBlock(BlockState state) {
        return state.isIn(ModTags.Blocks.SHRIEKER_TELEPORTER_ASSIGNABLE_BLOCKS);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound nbt = itemStack.getOrCreateNbt();

        if (nbt.contains(SHRIEKER_POS_KEY) && nbt.contains(SHRIEKER_DIMENSION_KEY)) {
            // Check if teleport is already in progress
            if (!nbt.contains("teleport_timer")) {

                Optional<RegistryKey<World>> storedDimension = getLodestoneDimension(nbt);
                RegistryKey<World> currentDimension = world.getRegistryKey();

                if (storedDimension.isPresent() && storedDimension.get() == currentDimension) {
                    BlockPos storedPos = NbtHelper.toBlockPos(nbt.getCompound(SHRIEKER_POS_KEY));
                    if (isAssignableBlock(world.getBlockState(storedPos))) {
                        // Start teleport countdown (40 ticks = 2 seconds)
                        nbt.putInt("teleport_timer", 40);

                        user.sendMessage(Text.literal("Teleport starting..."), false);
                        world.playSoundFromEntity(null, user, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.PLAYERS, 1.0F, 0.5F);
                        world.playSound(null, NbtHelper.toBlockPos(nbt.getCompound(SHRIEKER_POS_KEY)), SoundEvents.ENTITY_WARDEN_EMERGE, SoundCategory.BLOCKS, 1.0F, 1F);
                        PlayerEntity player = (PlayerEntity) user;
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 10));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 4));

                        return TypedActionResult.success(itemStack);
                    } else {
                        user.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.7F, 0.7F);
                        user.sendMessage(Text.literal("Block has been Destroyed!"), true);
                    }
                } else {
                    user.playSound(SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.PLAYERS, 0.7F, 0.7F);
                    user.sendMessage(Text.literal("You are in the wrong dimension!"), true);
                }
            }
        } else {
            user.sendMessage(Text.literal("No location set!"), true);
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

                if (!selected || !player.getInventory().contains(stack)) {
                    player.sendMessage(Text.literal("Teleport canceled."), true);
                    removeSpecificStatusEffects(player, StatusEffects.DARKNESS, StatusEffects.SLOWNESS);

                    // Remove the teleport timer from NBT
                    nbt.remove("teleport_timer");
                    return;
                }

                world.addParticle(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 0, 0.5, 0);

                // Countdown the timer
                if (timer >= 0) {
                    nbt.putInt("teleport_timer", timer - 1);
                } else {
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

    public void removeSpecificStatusEffects(PlayerEntity player, StatusEffect... effects) {
        for (StatusEffect effect : effects) {
            player.removeStatusEffect(effect);
        }
    }
}
