package io.github.seggan.swgv.util

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import java.util.*

inline fun ModelBatch.use(camera: Camera, block: ModelBatch.() -> Unit) {
    begin(camera)
    block()
    end()
}

fun <T> identityHashSet(): MutableSet<T> = Collections.newSetFromMap(IdentityHashMap<T, Boolean>())