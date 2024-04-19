package io.github.seggan.swgv

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.seggan.swgv.minecraft.block.Block
import io.github.seggan.swgv.minecraft.McMaterial
import io.github.seggan.swgv.minecraft.block.model.BlockModel
import io.github.seggan.swgv.util.use
import ktx.app.clearScreen
import org.lwjgl.opengl.GL40


class Main : ApplicationListener {

    private lateinit var camera: PerspectiveCamera
    private lateinit var camController: CameraController
    private lateinit var viewport: Viewport

    private lateinit var block: Block
    private lateinit var instance: ModelInstance
    private lateinit var modelBatch: ModelBatch

    private lateinit var environment: Environment

    override fun create() {
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(5f, 5f, 5f)
        camera.lookAt(0f, 0f, -10f)
        camera.near = 1f
        camera.far = 300f
        camera.update()

        viewport = ScreenViewport(camera)

        modelBatch = ModelBatch()
        block = Block(McMaterial.BEACON)
        instance = block.getInstance()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

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

        modelBatch.use(camera) {
            render(instance, environment)
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        modelBatch.dispose()
        block.dispose()
    }
}
