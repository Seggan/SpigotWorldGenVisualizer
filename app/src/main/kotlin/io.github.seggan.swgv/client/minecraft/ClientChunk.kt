package io.github.seggan.swgv.client.minecraft

import be.seeseemelk.mockbukkit.MockChunkData
import com.badlogic.gdx.graphics.g3d.ModelInstance
import io.github.seggan.swgv.client.minecraft.block.BlockCache
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.ChunkGenerator.ChunkData
import org.bukkit.generator.WorldInfo
import java.util.*

class ClientChunk(
    val world: World,
    val x: Int,
    val z: Int,
    val data: ChunkData
) {
    fun getBlocks(): List<ModelInstance> {
        val blocks = mutableListOf<ModelInstance>()
        for (y in 0 until world.maxHeight) {
            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    val block = data.getType(x, y, z)
                    if (ORTHOGONAL.all {
                        val relX = x + it.modX
                        val relY = y + it.modY
                        val relZ = z + it.modZ
                        if (relX < 0 || relX >= 16) return@all true
                        if (relY < 0 || relY >= world.maxHeight) return@all true
                        if (relZ < 0 || relZ >= 16) return@all true
                        !data.getType(relX, relY, relZ).isEmpty
                    }) continue
                    val creator = BlockCache[block] ?: continue
                    blocks += creator.createInstance(x, y, z)
                }
            }
        }
        return blocks
    }
}

private val ORTHOGONAL = arrayOf(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST)

fun ChunkGenerator.generateChunk(world: World, x: Int, z: Int): ClientChunk {
    val data = MockChunkData(world)
    val random = randoms.getOrPut(world.uid) { Random(world.seed) }
    generateNoise(world, random, x, z, data)
    generateSurface(world, random, x, z, data)
    generateBedrock(world, random, x, z, data)
    generateCaves(world, random, x, z, data)
    return ClientChunk(world, x, z, data)
}

private val randoms = mutableMapOf<UUID, Random>()


