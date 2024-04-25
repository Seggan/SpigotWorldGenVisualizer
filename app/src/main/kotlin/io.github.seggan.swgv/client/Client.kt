package io.github.seggan.swgv.client

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.seggan.swgv.client.minecraft.ClientChunk
import io.github.seggan.swgv.client.minecraft.block.BlockCache
import io.github.seggan.swgv.client.minecraft.generateChunk
import ktx.app.clearScreen
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator

class Client(
    private val world: World,
    private val generator: ChunkGenerator
) : ApplicationListener {

    private lateinit var camera: PerspectiveCamera
    private lateinit var camController: CameraController
    private lateinit var viewport: Viewport
    private lateinit var shadowLight: DirectionalShadowLight

    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment

    private val chunks = mutableMapOf<ClientChunk, List<ModelInstance>>()

    override fun create() {
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(0f, 70f, 0f)
        camera.lookAt(0f, 70f, 0f)
        camera.near = 1f
        camera.far = 300f
        camera.update()

        viewport = ScreenViewport(camera)

        modelBatch = ModelBatch()
        val chunk = generator.generateChunk(world, 0, 0)
        chunks[chunk] = chunk.getBlocks()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, .4f, .4f, .4f))
        shadowLight = DirectionalShadowLight(1024, 1024, 100f, 100f, .1f, 300f)
        shadowLight.set(1f, 1f, 1f, -1f, -1f, -1f)
        environment.add(shadowLight)
        environment.shadowMap = shadowLight

        camController = CameraController(camera)
        Gdx.input.inputProcessor = camController
        Gdx.input.isCursorCatched = true
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.update()
    }

    override fun render() {
        clearScreen(.5f, .5f, 1f, 1f)
        camController.update()
        camera.update(true)

        modelBatch.begin(camera)
        for (chunk in chunks.values) {
            for (block in chunk) {
                modelBatch.render(block, environment)
            }
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
