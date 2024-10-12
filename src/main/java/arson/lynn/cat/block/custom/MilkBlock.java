package arson.lynn.cat.block.custom;

import arson.lynn.cat.CatUtilities;
import arson.lynn.cat.block.ModBlocks;
import arson.lynn.cat.item.ModItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;


public class MilkBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static final int MAX_MILK_AMOUNT = 6;
    public static final IntProperty MILKS = IntProperty.of("milk_amount", 1, 6);

    private static VoxelShape SHAPE_1 = Block.createCuboidShape(6,0,6,10,9,10);
    private static VoxelShape SHAPE_2 = Block.createCuboidShape(3,0,6,13,9,10);
    private static VoxelShape SHAPE_3 = Block.createCuboidShape(3,0,3,13,9,13);
    private static VoxelShape SHAPE_4 = Block.createCuboidShape(3,0,3,13,9,13);
    private static VoxelShape SHAPE_5 = Block.createCuboidShape(1,0,3,15,9,13);
    private static VoxelShape SHAPE_6 = Block.createCuboidShape(1,0,3,15,9,13);

    private static VoxelShape SHAPE_1_90 = Block.createCuboidShape(6,0,6,10,9,10);
    private static VoxelShape SHAPE_2_90 = Block.createCuboidShape(6,0,3,10,9,13);
    private static VoxelShape SHAPE_3_90 = Block.createCuboidShape(3,0,3,13,9,13);
    private static VoxelShape SHAPE_4_90 = Block.createCuboidShape(3,0,3,13,9,13);
    private static VoxelShape SHAPE_5_90 = Block.createCuboidShape(3,0,1,13,9,15);
    private static VoxelShape SHAPE_6_90 = Block.createCuboidShape(3,0,1,13,9,15);

    public MilkBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(MILKS, Integer.valueOf(1)).with(FACING, Direction.NORTH)
        );
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MILKS, FACING);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);

        switch (facing) {
            case EAST, WEST -> {
                return switch (state.get(MILKS)) {
                    case 1 -> SHAPE_1;
                    case 2 -> SHAPE_2_90;
                    case 3 -> SHAPE_3;
                    case 4 -> SHAPE_4;
                    case 5 -> SHAPE_5_90;
                    case 6 -> SHAPE_6_90;
                    default -> SHAPE_1; // Default to original shape for now
                };
            }
            default -> {
                return switch (state.get(MILKS)) {
                    case 1 -> SHAPE_1;
                    case 2 -> SHAPE_2;
                    case 3 -> SHAPE_3;
                    case 4 -> SHAPE_4;
                    case 5 -> SHAPE_5;
                    case 6 -> SHAPE_6;
                    default -> SHAPE_1; // Default to original shape for now
                };
            }
        }
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return switch (state.get(MILKS)) {
            case 1 -> SHAPE_1;
            case 2 -> SHAPE_2;
            case 3 -> SHAPE_3;
            case 4 -> SHAPE_4;
            case 5 -> SHAPE_5;
            case 6 -> SHAPE_6;
            default -> SHAPE_1; // Default to original shape for now
        };
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem() && state.get(MILKS) < 6
                ? true
                : super.canReplace(state, context);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();
        BlockState existingState = context.getWorld().getBlockState(pos);

        if (existingState.isOf(this)) {
            // If the block is already placed and is of the same type, cycle the MILKS value
            return existingState.cycle(MILKS);
        } else {
            // If placing for the first time, set the MILKS and FACING properties
            return this.getDefaultState()
                    .with(MILKS, 1) // Set initial MILKS value (starting at 1)
                    .with(FACING, context.getHorizontalPlayerFacing().getOpposite());
        }
    }


    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

//    @Override
//    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
//        if(entity instanceof LivingEntity) {
//            ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
//        }
//
//        super.onSteppedOn(world, pos, state, entity);
//    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int currentMilk = state.get(MILKS);
        if (player.getAbilities().allowModifyWorld && player.getStackInHand(hand).isEmpty() && !player.isSneaking()) {
            player.clearStatusEffects();
            world.playSound(null, pos,
                    SoundEvents.ENTITY_GENERIC_DRINK, // Replace with your custom sound event
                    SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (currentMilk > 1) {
                // Decrease the number of candles
                world.setBlockState(pos, state.with(MILKS, currentMilk - 1));
            } else {
                // If this is the last candle, remove the block entirely
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
            return ActionResult.success(world.isClient);
        }
        else if(player.getAbilities().allowModifyWorld && (player.getStackInHand(hand).isEmpty() ||
                player.getStackInHand(hand).isOf(Item.fromBlock(ModBlocks.MILK))) && player.isSneaking()) {
            world.playSound(null, pos,
                    SoundEvents.ENTITY_ITEM_PICKUP, // Replace with your custom sound event
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (currentMilk > 1) {
                // Decrease the number of candles
                world.setBlockState(pos, state.with(MILKS, currentMilk - 1));
                player.giveItemStack(new ItemStack(ModBlocks.MILK));
            } else {
                // If this is the last candle, remove the block entirely
                player.giveItemStack(new ItemStack(ModBlocks.MILK));
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
            return ActionResult.success(world.isClient);
        }
        else {
            return ActionResult.PASS;
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }
}
