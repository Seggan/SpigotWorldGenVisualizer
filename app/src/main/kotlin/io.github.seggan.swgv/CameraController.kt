package io.github.seggan.swgv

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntIntMap
import kotlin.math.abs
import kotlin.math.sin


class CameraController(private val cam: Camera) : InputAdapter() {

    private var dragX = 0
    private var dragY = 0

    private val keys = IntIntMap()
    private val tmp = Vector3()

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!Gdx.input.isCursorCatched) return false
        if (dragX == 0 && dragY == 0) {
            dragX = screenX
            dragY = screenY
        }
        val x = dragX - screenX
        cam.rotate(Vector3.Y, x * ROT_SPEED)

        val y = sin((dragY - screenY) / 180f)
        cam.direction.y += y * ROT_SPEED * 5

        cam.update()
        dragX = screenX
        dragY = screenY
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        Gdx.input.isCursorCatched = true
        dragX = screenX
        dragY = screenY
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        keys.put(keycode, keycode)
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        keys.remove(keycode, 0)
        return true
    }

    fun update() {
        val dt = Gdx.graphics.deltaTime
        tmp.setZero()
        if (keys.containsKey(Keys.W)) {
            tmp.set(cam.direction)
            tmp.y = 0f
            tmp.nor().scl(MOVE_SPEED * dt)
        }
        if (keys.containsKey(Keys.S)) {
            tmp.set(cam.direction)
            tmp.y = 0f
            tmp.nor().scl(-MOVE_SPEED * dt)
        }
        if (keys.containsKey(Keys.A)) {
            tmp.set(cam.direction).crs(cam.up).nor().scl(-MOVE_SPEED * dt)
        }
        if (keys.containsKey(Keys.D)) {
            tmp.set(cam.direction).crs(cam.up).nor().scl(MOVE_SPEED * dt)
        }
        if (keys.containsKey(Keys.SPACE)) {
            tmp.set(Vector3.Y).scl(MOVE_SPEED * dt)
        }
        if (keys.containsKey(Keys.SHIFT_LEFT)) {
            tmp.set(Vector3.Y).scl(-MOVE_SPEED * dt)
        }
        if (keys.containsKey(Keys.ESCAPE)) {
            Gdx.input.isCursorCatched = false
        }
        cam.position.add(tmp)
    }

    companion object {
        private const val ROT_SPEED = 0.3f
        private const val MOVE_SPEED = 5f
    }
}