package io.github.seggan.swgv.minecraft.block

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import io.github.seggan.swgv.minecraft.McMaterial
import java.util.*

data class BlockTexture(
    val front: Texture,
    val back: Texture,
    val bottom: Texture,
    val top: Texture,
    val left: Texture,
    val right: Texture
) : Disposable {
    override fun dispose() {
        val disposed = Collections.newSetFromMap(IdentityHashMap<Texture, Boolean>())
        if (disposed.add(front)) front.dispose()
        if (disposed.add(back)) back.dispose()
        if (disposed.add(bottom)) bottom.dispose()
        if (disposed.add(top)) top.dispose()
        if (disposed.add(left)) left.dispose()
        if (disposed.add(right)) right.dispose()
    }

    companion object {

        fun singleTexture(texture: Texture) = BlockTexture(texture, texture, texture, texture, texture, texture)

        fun textureFor(material: McMaterial): BlockTexture {

        }
    }
}