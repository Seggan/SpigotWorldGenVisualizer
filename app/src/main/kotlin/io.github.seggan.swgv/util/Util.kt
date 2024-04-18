package io.github.seggan.swgv.util

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch

inline fun ModelBatch.use(camera: Camera, block: ModelBatch.() -> Unit) {
    begin(camera)
    block()
    end()
}