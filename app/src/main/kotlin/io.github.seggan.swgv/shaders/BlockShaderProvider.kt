package io.github.seggan.swgv.shaders

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import ktx.assets.toInternalFile

class BlockShaderProvider(config: DefaultShader.Config? = null) : DefaultShaderProvider(config) {

    private val shader = DefaultShader.Config().apply {
        //vertexShader = "vert.glsl".toInternalFile().readString()
        fragmentShader = "frag.glsl".toInternalFile().readString()
        numDirectionalLights = 1
    }

    override fun createShader(renderable: Renderable): Shader {
        return DefaultShader(renderable, shader)
    }
}