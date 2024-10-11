package arson.lynn.cat.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.message.MessageType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class Potion_TerracottaBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public Potion_TerracottaBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public List<StatusEffectInstance> block_effects = new ArrayList<StatusEffectInstance>();
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if(entity instanceof LivingEntity) {
            if(!block_effects.isEmpty()) {
                for (StatusEffectInstance effect : block_effects) {
                    ((LivingEntity) entity).addStatusEffect(effect);
                }
            }
        }

        super.onSteppedOn(world, pos, state, entity);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ItemStack itemInHand = player.getStackInHand(hand);
            //world.breakBlock(pos, false);
            if (itemInHand.getItem() instanceof PotionItem) {
                block_effects.clear();
                world.playSound(null, pos,
                        SoundEvents.BLOCK_BREWING_STAND_BREW, // Replace with your custom sound event
                        SoundCategory.BLOCKS, 1.0F, 1.0F);
                Potion potion = PotionUtil.getPotion(itemInHand);
                List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(itemInHand);

                // Apply the effects of the potion to the player
                for (StatusEffectInstance effect : effects) {
                    block_effects.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()));
                    player.sendMessage(Text.literal(String.valueOf(effect.getEffectType().getTranslationKey())), true);
                }

                // Optionally: consume the potion item
                if(!player.isCreative()) {
                    itemInHand.decrement(1);
                }
            }
            else if(itemInHand.getItem() == Items.STICK) {
                world.playSound(null, pos,
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, // Replace with your custom sound event
                        SoundCategory.BLOCKS, 1.0F, 1.0F);
                for (StatusEffectInstance effect : block_effects) {
                    player.sendMessage(Text.literal(String.valueOf(effect.getEffectType().getTranslationKey())), true);
                }
            }
            else {
                return ActionResult.PASS; // Allow other interactions, like placing blocks next to it
            }
        }



        return ActionResult.PASS;
    }
}
