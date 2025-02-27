package ru.betterend.mixin.common;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import ru.bclib.util.StructureHelper;
import ru.betterend.BetterEnd;
import ru.betterend.util.WorldDataUtil;
import ru.betterend.world.generator.GeneratorOptions;

@Mixin(EndPodiumFeature.class)
public class EndPodiumFeatureMixin {
	@Final
	@Shadow
	private boolean active;
	
	@Inject(method = "place", at = @At("HEAD"), cancellable = true)
	private void be_place(WorldGenLevel world, ChunkGenerator generator, Random random, BlockPos blockPos, NoneFeatureConfiguration config, CallbackInfoReturnable<Boolean> info) {
		if (!GeneratorOptions.hasPortal()) {
			info.setReturnValue(false);
			info.cancel();
		}
		else if (GeneratorOptions.replacePortal()) {
			blockPos = be_updatePos(blockPos, world);
			StructureTemplate structure = StructureHelper.readStructure(BetterEnd.makeID(active ? "portal/end_portal_active" : "portal/end_portal_inactive"));
			BlockPos size = structure.getSize();
			blockPos = blockPos.offset(-(size.getX() >> 1), -1, -(size.getZ() >> 1));
			structure.placeInWorldChunk(world, blockPos, new StructurePlaceSettings(), random);
			info.setReturnValue(true);
			info.cancel();
		}
	}
	
	@ModifyVariable(method = "place", ordinal = 0, at = @At("HEAD"))
	private BlockPos be_setPosOnGround(BlockPos blockPos, WorldGenLevel world) {
		return be_updatePos(blockPos, world);
	}
	
	private BlockPos be_updatePos(BlockPos blockPos, WorldGenLevel world) {
		if (GeneratorOptions.useNewGenerator()) {
			BlockPos pos = GeneratorOptions.getPortalPos();
			if (pos.equals(BlockPos.ZERO)) {
				int y = world.getChunk(0, 0, ChunkStatus.FULL).getHeight(Types.WORLD_SURFACE, blockPos.getX(), blockPos.getZ());
				if (y < 1) {
					y = 65;
				}
				pos = new BlockPos(pos.getX(), y, pos.getZ());
				GeneratorOptions.setPortalPos(pos);
				WorldDataUtil.getRootTag().put("portal", NbtUtils.writeBlockPos(pos));
				WorldDataUtil.saveFile();
			}
			return pos;
		}
		return blockPos;
	}
}
