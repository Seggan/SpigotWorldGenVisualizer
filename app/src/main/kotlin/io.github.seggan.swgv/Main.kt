package io.github.seggan.swgv

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import io.github.seggan.swgv.util.use
import ktx.app.clearScreen


class Main : ApplicationListener {

    private lateinit var camera: PerspectiveCamera
    private lateinit var camController: CameraController

    private lateinit var model: Model
    private lateinit var instance: ModelInstance
    private lateinit var modelBatch: ModelBatch

    private lateinit var environment: Environment

    override fun create() {
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(10f, 10f, 10f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 1f
        camera.far = 300f
        camera.update()

        modelBatch = ModelBatch()
        model = ModelBuilder().createBox(
            5f, 5f, 5f,
            Material(ColorAttribute.createDiffuse(Color.GREEN)),
            (Usage.Position or Usage.Normal).toLong()
        )
        instance = ModelInstance(model)

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        camController = CameraController(camera)
        Gdx.input.inputProcessor = camController
        Gdx.input.isCursorCatched = true
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        camController.update()

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
        model.dispose()
    }
}
