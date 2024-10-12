package arson.lynn.cat.item.custom;

import arson.lynn.cat.block.ModBlocks;
import com.mojang.logging.LogUtils;

import java.util.Optional;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.swing.*;

public class TeleporterItem extends Item implements Vanishable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String LODESTONE_POS_KEY = "LodestonePos";
    public static final String SHRIEKER_POS_KEY = "ShriekerPos";
    public static final String LODESTONE_DIMENSION_KEY = "LodestoneDimension";
    public static final String LODESTONE_TRACKED_KEY = "LodestoneTracked";

    public static int x = 0;
    public static int y = 0;
    public static int z = 0;

    public TeleporterItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean hasLodestone(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && (nbtCompound.contains("LodestoneDimension") || nbtCompound.contains(SHRIEKER_POS_KEY));
    }

    private static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get("LodestoneDimension")).result();
    }

    @Nullable
    public static GlobalPos createLodestonePos(NbtCompound nbt) {
        boolean bl = nbt.contains(SHRIEKER_POS_KEY);
        if (bl) {
            Optional<RegistryKey<World>> optional = getLodestoneDimension(nbt);
            if (optional.isPresent()) {
                BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound(SHRIEKER_POS_KEY));
                x = blockPos.getX();
                y = blockPos.getY();
                z = blockPos.getZ();
                return GlobalPos.create((RegistryKey<World>) optional.get(), blockPos);
            }
        }

        return null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if (!world.isClient) {
            ItemStack itemStack = context.getPlayer().getStackInHand(context.getHand());

            // Store block position in NBT
            NbtCompound nbt = itemStack.getOrCreateNbt();
            nbt.put(SHRIEKER_POS_KEY, NbtHelper.fromBlockPos(blockPos));
            nbt.putInt("storedX", blockPos.getX());
            nbt.putInt("storedY", blockPos.getY());
            nbt.putInt("storedZ", blockPos.getZ());

            // Optionally send feedback to player
            context.getPlayer().sendMessage(Text.literal("Block position stored: " + blockPos), true);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && nbt.contains(SHRIEKER_POS_KEY)) {
            BlockPos storedPos = NbtHelper.toBlockPos(nbt.getCompound(SHRIEKER_POS_KEY));

            user.sendMessage(Text.literal("Block position stored: " + storedPos), true);
            // Use the storedPos for something...
            world.playSoundFromEntity(null, user, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            user.teleport(storedPos.getX(), storedPos.getY() + 1, storedPos.getZ());

            if(!user.isCreative()) {
                user.getItemCooldownManager().set(this, 100);
            }

        }

        return TypedActionResult.success(itemStack);
    }
}
