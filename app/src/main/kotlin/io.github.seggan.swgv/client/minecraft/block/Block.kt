package io.github.seggan.swgv.client.minecraft.block

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Disposable
import io.github.seggan.swgv.client.minecraft.McMaterial
import io.github.seggan.swgv.client.minecraft.block.model.BlockModel
import io.github.seggan.swgv.client.util.identityHashSet

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

    fun createInstance(x: Int, y: Int, z: Int): ModelInstance {
        val model = ModelInstance(model)
        model.transform.setToTranslation(x.toFloat(), y.toFloat(), z.toFloat())
        return model
    }

    override fun dispose() {
        model.dispose()
        for (texture in textures) {
            texture.dispose()
        }
    }
}