package io.github.seggan.swgv.client.minecraft

import be.seeseemelk.mockbukkit.MockChunkData
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelCache
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Disposable
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
) : Disposable {
    val model: ModelCache by lazy {
        val cx = x * 16
        val cz = z * 16
        val cache = ModelCache()
        cache.begin()
        for (x in 0 until 16) {
            for (y in world.minHeight until world.maxHeight) {
                for (z in 0 until 16) {
                    val block = data.getType(x, y, z)
                    val faces = BlockCache[block]
                    for (face in faces) {
                        if (
                            data.hasSolidBlock(
                                world,
                                x + face.cullFace.modX,
                                y + face.cullFace.modY,
                                z + face.cullFace.modZ
                            )
                        ) continue
                        val model = ModelInstance(face.model)
                        model.transform.setToTranslation(cx + x.toFloat(), y.toFloat(), cz + z.toFloat())
                        cache.add(model)
                    }
                }
            }
        }
        cache.end()
        cache
    }

    override fun dispose() {
        model.dispose()
    }
}

fun ChunkData.hasSolidBlock(info: WorldInfo, x: Int, y: Int, z: Int): Boolean {
    if (x < 0 || x >= 16 || z < 0 || z >= 16 || y < info.minHeight || y >= info.maxHeight) return true
    return getType(x, y, z).isSolid
}

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


