package ru.betterend.world.structures.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import ru.bclib.util.MHelper;
import ru.betterend.world.structures.piece.LakePiece;

public class MegaLakeStructure extends FeatureBaseStructure {
	@Override
	public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
		return SDFStructureStart::new;
	}
	
	public static class SDFStructureStart extends StructureStart<NoneFeatureConfiguration> {
		public SDFStructureStart(StructureFeature<NoneFeatureConfiguration> feature, int chunkX, int chunkZ, BoundingBox box, int references, long seed) {
			super(feature, chunkX, chunkZ, box, references, seed);
		}

		@Override
		public void generatePieces(RegistryAccess registryManager, ChunkGenerator chunkGenerator, StructureManager manager, int chunkX, int chunkZ, Biome biome, NoneFeatureConfiguration config) {
			int x = (chunkX << 4) | MHelper.randRange(4, 12, random);
			int z = (chunkZ << 4) | MHelper.randRange(4, 12, random);
			int y = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG);
			if (y > 5) {
				float radius = MHelper.randRange(32, 64, random);
				float depth = MHelper.randRange(7, 15, random);
				LakePiece piece = new LakePiece(new BlockPos(x, y, z), radius, depth, random, biome);
				this.pieces.add(piece);
			}
			this.calculateBoundingBox();
		}
	}
}
