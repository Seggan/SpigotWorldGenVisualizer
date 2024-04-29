package io.github.seggan.swgv.client

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.seggan.swgv.client.minecraft.ClientChunk
import io.github.seggan.swgv.client.minecraft.block.BlockCache
import io.github.seggan.swgv.client.minecraft.generateChunk
import io.github.seggan.swgv.client.util.distanceSquared
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import ktx.app.clearScreen
import ktx.async.AsyncExecutorDispatcher
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.async.skipFrame
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock

class Client(
    private val world: World,
    private val generator: ChunkGenerator
) : ApplicationListener {

    private val dispatcher = newSingleThreadAsyncContext("chunkloader")

    private lateinit var camera: PerspectiveCamera
    private lateinit var camController: CameraController
    private lateinit var viewport: Viewport
    private lateinit var light: DirectionalShadowLight

    private lateinit var modelBatch: ModelBatch
    private lateinit var shadowBatch: ModelBatch
    private lateinit var environment: Environment

    private val chunks = ConcurrentLinkedQueue<ClientChunk>()

    override fun create() {
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(0f, 70f, 0f)
        camera.lookAt(1f, 70f, 1f)
        camera.near = 1f
        camera.far = 300f
        camera.update()

        viewport = ScreenViewport(camera)

        modelBatch = ModelBatch()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, .4f, .4f, .4f))
        light = DirectionalShadowLight(1024, 1024, 100f, 100f, .1f, 300f)
        light.set(1f, 1f, 1f, 0f, -1f, 0f)
        environment.add(light)
        environment.shadowMap = light

        shadowBatch = ModelBatch(DepthShaderProvider())

        KtxAsync.initiate()
        KtxAsync.launch {
            while (true) {
                val cx = camera.position.x.toInt() / 16
                val cz = camera.position.z.toInt() / 16
                val toDispose = withContext(dispatcher) {
                    for (x in -RENDER_DISTANCE..RENDER_DISTANCE) {
                        for (z in -RENDER_DISTANCE..RENDER_DISTANCE) {
                            val realX = cx + x
                            val realZ = cz + z
                            val chunk = chunks.find { it.x == realX && it.z == realZ }
                            if (chunk == null) {
                                chunks += generator.generateChunk(world, realX, realZ)
                            }
                        }
                    }
                    val toDispose = mutableListOf<ClientChunk>()
                    for (chunk in chunks) {
                        if (distanceSquared(chunk.x, chunk.z, cx, cz) > 4 * RENDER_DISTANCE * RENDER_DISTANCE) {
                            toDispose += chunk
                        }
                    }
                    toDispose
                }
                for (chunk in toDispose) {
                    chunks.remove(chunk)
                    chunk.dispose()
                }
            }
        }

        camController = CameraController(camera)
        Gdx.input.inputProcessor = camController
        Gdx.input.isCursorCatched = true
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.update()
    }

    override fun render() {
        light.begin(camera.position.cpy().add(0f, 100f, 0f), camera.direction)
        shadowBatch.begin(light.camera)
        for (chunk in chunks) {
            shadowBatch.render(chunk.model)
        }
        shadowBatch.end()
        light.end()

        clearScreen(.5f, .5f, 1f, 1f)
        camController.update()
        camera.update(true)

        modelBatch.begin(camera)
        for (chunk in chunks) {
            modelBatch.render(chunk.model, environment)
        }
        modelBatch.end()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        modelBatch.dispose()
        BlockCache.dispose()
    }
}

private const val RENDER_DISTANCE = 8
