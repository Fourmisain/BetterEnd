package ru.betterend.world.features.bushes;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;
import ru.bclib.api.TagAPI;
import ru.bclib.sdf.SDF;
import ru.bclib.sdf.operator.SDFDisplacement;
import ru.bclib.sdf.operator.SDFScale3D;
import ru.bclib.sdf.operator.SDFSubtraction;
import ru.bclib.sdf.operator.SDFTranslate;
import ru.bclib.sdf.primitive.SDFSphere;
import ru.bclib.util.MHelper;
import ru.betterend.noise.OpenSimplexNoise;
import ru.betterend.util.BlocksHelper;
import ru.betterend.world.features.DefaultFeature;

public class BushWithOuterFeature extends DefaultFeature {
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Function<BlockState, Boolean> REPLACE;
	private final Block outer_leaves;
	private final Block leaves;
	private final Block stem;

	public BushWithOuterFeature(Block leaves, Block outer_leaves, Block stem) {
		this.outer_leaves = outer_leaves;
		this.leaves = leaves;
		this.stem = stem;
	}

	@Override
	public boolean place(WorldGenLevel world, ChunkGenerator chunkGenerator, Random random, BlockPos pos,
			NoneFeatureConfiguration config) {
		if (!world.getBlockState(pos.below()).getBlock().is(TagAPI.END_GROUND)
				&& !world.getBlockState(pos.above()).getBlock().is(TagAPI.END_GROUND))
			return false;

		float radius = MHelper.randRange(1.8F, 3.5F, random);
		OpenSimplexNoise noise = new OpenSimplexNoise(random.nextInt());
		SDF sphere = new SDFSphere().setRadius(radius).setBlock(this.leaves);
		sphere = new SDFScale3D().setScale(1, 0.5F, 1).setSource(sphere);
		sphere = new SDFDisplacement().setFunction((vec) -> {
			return (float) noise.eval(vec.x() * 0.2, vec.y() * 0.2, vec.z() * 0.2) * 3;
		}).setSource(sphere);
		sphere = new SDFDisplacement().setFunction((vec) -> {
			return MHelper.randRange(-2F, 2F, random);
		}).setSource(sphere);
		sphere = new SDFSubtraction().setSourceA(sphere)
				.setSourceB(new SDFTranslate().setTranslate(0, -radius, 0).setSource(sphere));
		sphere.setReplaceFunction(REPLACE);
		sphere.addPostProcess((info) -> {
			if (info.getState().getBlock() instanceof LeavesBlock) {
				int distance = info.getPos().distManhattan(pos);
				if (distance < 7) {
					return info.getState().setValue(LeavesBlock.DISTANCE, distance);
				} else {
					return AIR;
				}
			}
			return info.getState();
		}).addPostProcess((info) -> {
			if (info.getState().getBlock() instanceof LeavesBlock) {
				MHelper.shuffle(DIRECTIONS, random);
				for (Direction dir : DIRECTIONS) {
					if (info.getState(dir).isAir()) {
						info.setBlockPos(info.getPos().relative(dir),
								outer_leaves.defaultBlockState().setValue(BlockStateProperties.FACING, dir));
					}
				}
			}
			return info.getState();
		});
		sphere.fillRecursive(world, pos);
		BlocksHelper.setWithoutUpdate(world, pos, stem);
		for (Direction d : Direction.values()) {
			BlockPos p = pos.relative(d);
			if (world.isEmptyBlock(p)) {
				if (leaves instanceof LeavesBlock) {
					BlocksHelper.setWithoutUpdate(world, p,
							leaves.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1));
				} else {
					BlocksHelper.setWithoutUpdate(world, p, leaves.defaultBlockState());
				}
			}
		}

		return true;
	}

	static {
		REPLACE = (state) -> {
			if (state.getMaterial().equals(Material.PLANT)) {
				return true;
			}
			return state.getMaterial().isReplaceable();
		};
	}
}
