package net.nerdorg.vortexmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nerdorg.vortexmod.block.entity.TardisCoreBlockEntity;
import net.nerdorg.vortexmod.sound.ModSounds;
import net.nerdorg.vortexmod.util.CoreUtil;
import net.nerdorg.vortexmod.util.LogUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.Random;

public class TardisThrottleBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape NORTH_AABB = Block.box(5.5D, 5.0D, 11.0D, 10.5D, 11.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.5D, 5.0D, 0.0D, 10.5D, 11.0D, 5.0D);
    protected static final VoxelShape WEST_AABB = Block.box(11.0D, 5.0D, 5.5D, 16.0D, 11.0D, 10.5D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 5.0D, 5.5D, 5.0D, 11.0D, 10.5D);
    protected static final VoxelShape UP_AABB_Z = Block.box(5.5D, 0.0D, 5.0D, 10.5D, 5.0D, 11.0D);
    protected static final VoxelShape UP_AABB_X = Block.box(5.0D, 0.0D, 5.5D, 11.0D, 5.0D, 10.5D);
    protected static final VoxelShape DOWN_AABB_Z = Block.box(5.5D, 11.0D, 5.0D, 10.5D, 16.0D, 11.0D);
    protected static final VoxelShape DOWN_AABB_X = Block.box(5.0D, 11.0D, 5.5D, 11.0D, 16.0D, 10.5D);

    private BlockPos linkedPos;

    public TardisThrottleBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.FALSE).setValue(FACE, AttachFace.WALL));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch ((AttachFace)pState.getValue(FACE)) {
            case FLOOR:
                switch (pState.getValue(FACING).getAxis()) {
                    case X:
                        return UP_AABB_X;
                    case Z:
                    default:
                        return UP_AABB_Z;
                }
            case WALL:
                switch ((Direction)pState.getValue(FACING)) {
                    case EAST:
                        return EAST_AABB;
                    case WEST:
                        return WEST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
            case CEILING:
            default:
                switch (pState.getValue(FACING).getAxis()) {
                    case X:
                        return DOWN_AABB_X;
                    case Z:
                    default:
                        return DOWN_AABB_Z;
                }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
            } else {
                blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
            }

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel instanceof ServerLevel serverLevel) {
            if (VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pPos.getX(), pPos.getY(), pPos.getZ()) == null) {
                LogUtil.doFailureMessage(pPlayer, "Please assemble the TARDIS before flight. You can do this by right clicking on the core.");
                return InteractionResult.CONSUME;
            }
        }

        TardisCoreBlockEntity tardisCoreBlockEntity = CoreUtil.getClosestCoreBlockEntity(pPos);

        if (tardisCoreBlockEntity != null) {
            if (pLevel.isClientSide) {
                BlockState blockstate1 = pState.cycle(POWERED);
                if (blockstate1.getValue(POWERED)) {
                    makeParticle(blockstate1, pLevel, pPos, 1.0F);
                }
                return InteractionResult.SUCCESS;
            } else {
                Random random = new Random();

                BlockState blockstate = this.pull(pState, pLevel, pPos);
                pLevel.gameEvent(pPlayer, blockstate.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pPos);
                if (blockstate.getValue(POWERED)) {
                    pPlayer.displayClientMessage(Component.literal("Throttle Enabled"), true);
                    tardisCoreBlockEntity.setMoveToTarget(true);
                    pLevel.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.THROTTLE_SOUND.get(), SoundSource.BLOCKS, 1f, random.nextFloat(1f, 1.2f), 0);
                } else {
                    pPlayer.displayClientMessage(Component.literal("Throttle Disabled"), true);
                    tardisCoreBlockEntity.setMoveToTarget(false);
                    pLevel.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.THROTTLE_SOUND.get(), SoundSource.BLOCKS, 1f, random.nextFloat(0.7f, 0.9f), 0);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    public BlockPos getLinkedPos() {
        return this.linkedPos;
    }

    public void setLinkedPos(BlockPos linkedPos) {
        this.linkedPos = linkedPos;
    }

    public BlockState pull(BlockState pState, Level pLevel, BlockPos pPos) {
        pState = pState.cycle(POWERED);
        pLevel.setBlock(pPos, pState, 3);
        return pState;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(POWERED) && pRandom.nextFloat() < 0.25F) {
            makeParticle(pState, pLevel, pPos, 0.5F);
        }
    }

    private static void makeParticle(BlockState pState, LevelAccessor pLevel, BlockPos pPos, float pAlpha) {
        Direction direction = pState.getValue(FACING).getOpposite();
        Direction direction1 = getConnectedDirection(pState).getOpposite();
        double d0 = (double)pPos.getX() + 0.5D + 0.1D * (double)direction.getStepX() + 0.2D * (double)direction1.getStepX();
        double d1 = (double)pPos.getY() + 0.5D + 0.1D * (double)direction.getStepY() + 0.2D * (double)direction1.getStepY();
        double d2 = (double)pPos.getZ() + 0.5D + 0.1D * (double)direction.getStepZ() + 0.2D * (double)direction1.getStepZ();
        pLevel.addParticle(new DustParticleOptions(new Vector3f(80, 167, 167), pAlpha), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.vortexmod.throttle_block.tooltip"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACE, FACING, POWERED);
        super.createBlockStateDefinition(pBuilder);
    }
}
