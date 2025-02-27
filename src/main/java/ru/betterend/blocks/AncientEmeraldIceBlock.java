package ru.betterend.blocks;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import ru.bclib.util.MHelper;
import ru.betterend.blocks.basis.BlockBase;
import ru.betterend.registry.EndBlocks;
import ru.betterend.registry.EndParticles;
import ru.betterend.util.BlocksHelper;

public class AncientEmeraldIceBlock extends BlockBase {
	public AncientEmeraldIceBlock() {
		super(FabricBlockSettings.copyOf(Blocks.BLUE_ICE).randomTicks());
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		Direction dir = BlocksHelper.randomDirection(random);
		
		if (random.nextBoolean()) {
			int x = MHelper.randRange(-2, 2, random);
			int y = MHelper.randRange(-2, 2, random);
			int z = MHelper.randRange(-2, 2, random);
			BlockPos p = pos.offset(x, y, z);
			if (world.getBlockState(p).is(Blocks.WATER)) {
				world.setBlockAndUpdate(p, EndBlocks.EMERALD_ICE.defaultBlockState());
				makeParticles(world, p, random);
			}
		}
		
		pos = pos.relative(dir);
		state = world.getBlockState(pos);
		if (state.is(Blocks.WATER)) {
			world.setBlockAndUpdate(pos, EndBlocks.EMERALD_ICE.defaultBlockState());
			makeParticles(world, pos, random);
		}
		else if (state.is(EndBlocks.EMERALD_ICE)) {
			world.setBlockAndUpdate(pos, EndBlocks.DENSE_EMERALD_ICE.defaultBlockState());
			makeParticles(world, pos, random);
		}
	}
	
	private void makeParticles(ServerLevel world, BlockPos pos, Random random) {
		world.sendParticles(EndParticles.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
		if (tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) != 0) {
			return Collections.singletonList(new ItemStack(this));
		}
		else {
			return Collections.emptyList();
		}
	}
}
