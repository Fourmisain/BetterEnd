package ru.betterend.world.features;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ru.bclib.util.MHelper;
import ru.betterend.blocks.BlockProperties;
import ru.betterend.blocks.BlockProperties.TripleShape;
import ru.betterend.registry.EndBlocks;
import ru.betterend.util.BlocksHelper;

public class FilaluxFeature extends SkyScatterFeature {
	public FilaluxFeature() {
		super(10);
	}

	@Override
	public void generate(WorldGenLevel world, Random random, BlockPos blockPos) {
		BlockState vine = EndBlocks.FILALUX.defaultBlockState();
		BlockState wings = EndBlocks.FILALUX_WINGS.defaultBlockState();
		BlocksHelper.setWithoutUpdate(world, blockPos, EndBlocks.FILALUX_LANTERN);
		BlocksHelper.setWithoutUpdate(world, blockPos.above(), wings.setValue(BlockStateProperties.FACING, Direction.UP));
		for (Direction dir: BlocksHelper.HORIZONTAL) {
			BlocksHelper.setWithoutUpdate(world, blockPos.relative(dir), wings.setValue(BlockStateProperties.FACING, dir));
		}
		int length = MHelper.randRange(1, 3, random);
		for (int i = 1; i <= length; i++) {
			TripleShape shape = length > 1 ? TripleShape.TOP : TripleShape.BOTTOM;
			if (i > 1) {
				shape = i == length ? TripleShape.BOTTOM : TripleShape.MIDDLE;
			}
			BlocksHelper.setWithoutUpdate(world, blockPos.below(i), vine.setValue(BlockProperties.TRIPLE_SHAPE, shape));
		}
	}
}
