package io.github.seggan.swgv.shaders

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.assets.toInternalFile

class BlockShader : Shader {

    private lateinit var program: ShaderProgram

    private lateinit var camera: Camera
    private lateinit var context: RenderContext

    private var u_projViewTrans = 0
    private var u_worldTrans = 0
    private var u_texture = 0

    override fun init() {
        val vert = "vert.glsl".toInternalFile().readString()
        val frag = "frag.glsl".toInternalFile().readString()
        program = ShaderProgram(vert, frag)
        if (!program.isCompiled) {
            throw GdxRuntimeException("Shader failed to compile: ${program.log}")
        }
        u_projViewTrans = program.getUniformLocation("u_projViewTrans")
        u_worldTrans = program.getUniformLocation("u_worldTrans")
        u_texture = program.getUniformLocation("u_texture")
    }

    override fun render(renderable: Renderable) {
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform)
        renderable.meshPart.render(program)
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.camera = camera
        this.context = context
        program.bind()
        program.setUniformMatrix(u_projViewTrans, camera.combined)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)
    }

    override fun end() {
    }

    override fun canRender(instance: Renderable): Boolean = true

    override fun compareTo(other: Shader): Int = 0

    override fun dispose() {
        program.dispose()
    }
}