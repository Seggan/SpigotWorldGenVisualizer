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
import io.github.seggan.swgv.minecraft.block.model.BlockModel
import io.github.seggan.swgv.minecraft.block.model.ModelTexture
import io.github.seggan.swgv.util.identityHashSet
import ktx.assets.toInternalFile
import java.util.Collections
import java.util.IdentityHashMap

class Block(private val mcMaterial: McMaterial) : Disposable {

    private val model: Model
    private val textures = identityHashSet<Texture>()

    init {
        val builder = ModelBuilder()
        builder.begin()
        val blockModel = BlockModel.getModel(mcMaterial.name.lowercase())
        for (element in blockModel.elements) {
            textures.addAll(element.render(builder, blockModel))
        }
        model = builder.end()
    }

    fun getInstance() = ModelInstance(model)

    override fun dispose() {
        model.dispose()
        for (texture in textures) {
            texture.dispose()
        }
    }
}