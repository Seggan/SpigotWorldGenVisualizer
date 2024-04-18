package io.github.seggan.swgv.minecraft.block

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Disposable
import io.github.seggan.swgv.minecraft.McMaterial
import ktx.assets.toInternalFile

class Block(private val mcMaterial: McMaterial) : Disposable {

    private val model: Model
    private val texture = BlockTexture.singleTexture(Texture("minecraft/textures/block/${mcMaterial.name.lowercase()}.png".toInternalFile()))

    init {
        val builder = ModelBuilder()
        val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

        builder.begin()
        builder.part(
            "front",
            GL20.GL_TRIANGLES,
            attr,
            Material(TextureAttribute.createDiffuse(texture.front)),
        ).rect(-2f, -2f, -2f, -2f, 2f, -2f, 2f, 2f, -2f, 2f, -2f, -2f, 0f, 0f, -1f)
        builder.part(
            "back",
            GL20.GL_TRIANGLES,
            attr,
            Material(TextureAttribute.createDiffuse(texture.back)),
        ).rect(-2f, 2f, 2f, -2f, -2f, 2f, 2f, -2f, 2f, 2f, 2f, 2f, 0f, 0f, 1f)
        builder.part(
            "bottom",
            GL20.GL_TRIANGLES,
            attr,
            Material(TextureAttribute.createDiffuse(texture.bottom)),
        ).rect(-2f, -2f, 2f, -2f, -2f, -2f, 2f, -2f, -2f, 2f, -2f, 2f, 0f, -1f, 0f)
        builder.part(
            "top",
            GL20.GL_TRIANGLES,
            attr,
            Material(TextureAttribute.createDiffuse(texture.top)),
        ).rect(-2f, 2f, -2f, -2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, -2f, 0f, 1f, 0f)
        builder.part(
            "left",
            GL20.GL_TRIANGLES,
            attr,
            Material(TextureAttribute.createDiffuse(texture.left)),
        ).rect(-2f, -2f, 2f, -2f, 2f, 2f, -2f, 2f, -2f, -2f, -2f, -2f, -1f, 0f, 0f)
        builder.part(
            "right",
            GL20.GL_TRIANGLES,
            attr,
            Material(TextureAttribute.createDiffuse(texture.right)),
        ).rect(2f, -2f, -2f, 2f, 2f, -2f, 2f, 2f, 2f, 2f, -2f, 2f, 1f, 0f, 0f)
        model = builder.end()
    }

    fun getInstance() = ModelInstance(model)

    override fun dispose() {
        model.dispose()
        texture.dispose()
    }
}