package io.github.seggan.testplugin

import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.bukkit.util.noise.SimplexOctaveGenerator
import java.util.*

object TestGenerator : ChunkGenerator() {

    private lateinit var noise: SimplexOctaveGenerator

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        noise = SimplexOctaveGenerator(worldInfo.seed, 8)
        noise.setScale(1 / 64.0)
        val cx = chunkX * 16.0
        val cz = chunkZ * 16.0
        for (x in 0..15) {
            for (z in 0..15) {
                val height = (noise.noise(cx + x, cz + z, 0.5, 0.5, true) * 15 + 50).toInt()
                for (y in 0 until height) {
                    chunkData.setBlock(x, y, z, Material.STONE)
                }
            }
        }
    }
}