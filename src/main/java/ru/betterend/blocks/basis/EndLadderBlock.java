package ru.betterend.blocks.basis;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.bclib.client.models.BlockModelProvider;
import ru.bclib.client.models.ModelsHelper;
import ru.betterend.client.models.Patterns;
import ru.betterend.client.render.ERenderLayer;
import ru.betterend.interfaces.IRenderTypeable;
import ru.betterend.util.BlocksHelper;

public class EndLadderBlock extends BlockBaseNotFull implements IRenderTypeable, BlockModelProvider {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape EAST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_SHAPE = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);

	public EndLadderBlock(Block block) {
		super(FabricBlockSettings.copyOf(block).noOcclusion());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(FACING);
		stateManager.add(WATERLOGGED);
	}

	public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
		switch (state.getValue(FACING)) {
		case SOUTH:
			return SOUTH_SHAPE;
		case WEST:
			return WEST_SHAPE;
		case EAST:
			return EAST_SHAPE;
		default:
			return NORTH_SHAPE;
		}
	}

	private boolean canPlaceOn(BlockGetter world, BlockPos pos, Direction side) {
		BlockState blockState = world.getBlockState(pos);
		return !blockState.isSignalSource() && blockState.isFaceSturdy(world, pos, side);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = (Direction) state.getValue(FACING);
		return this.canPlaceOn(world, pos.relative(direction.getOpposite()), direction);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState,
			LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if (facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(world, pos)) {
			return Blocks.AIR.defaultBlockState();
		} else {
			if ((Boolean) state.getValue(WATERLOGGED)) {
				world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
			}

			return super.updateShape(state, facing, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState blockState2;
		if (!ctx.replacingClickedOnBlock()) {
			blockState2 = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite()));
			if (blockState2.getBlock() == this && blockState2.getValue(FACING) == ctx.getClickedFace()) {
				return null;
			}
		}

		blockState2 = this.defaultBlockState();
		LevelReader worldView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		Direction[] var6 = ctx.getNearestLookingDirections();
		int var7 = var6.length;

		for (int var8 = 0; var8 < var7; ++var8) {
			Direction direction = var6[var8];
			if (direction.getAxis().isHorizontal()) {
				blockState2 = (BlockState) blockState2.setValue(FACING, direction.getOpposite());
				if (blockState2.canSurvive(worldView, blockPos)) {
					return (BlockState) blockState2.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
				}
			}
		}

		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return BlocksHelper.rotateHorizontal(state, rotation, FACING);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return BlocksHelper.mirrorHorizontal(state, mirror, FACING);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (Boolean) state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public ERenderLayer getRenderLayer() {
		return ERenderLayer.CUTOUT;
	}

	@Override
	public BlockModel getItemModel(ResourceLocation blockId) {
		return ModelsHelper.createBlockItem(blockId);
	}

	@Override
	public @Nullable BlockModel getBlockModel(ResourceLocation blockId, BlockState blockState) {
		Optional<String> pattern = Patterns.createJson(Patterns.BLOCK_LADDER, blockId.getPath());
		return ModelsHelper.fromPattern(pattern);
	}

	@Override
	public UnbakedModel getModelVariant(ResourceLocation stateId, BlockState blockState, Map<ResourceLocation, UnbakedModel> modelCache) {
		ResourceLocation modelId = new ResourceLocation(stateId.getNamespace(), "block/" + stateId.getPath());
		registerBlockModel(stateId, modelId, blockState, modelCache);
		return ModelsHelper.createFacingModel(modelId, blockState.getValue(FACING), false, true);
	}
}
