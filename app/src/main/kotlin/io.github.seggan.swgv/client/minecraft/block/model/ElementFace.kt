package io.github.seggan.swgv.client.minecraft.block.model

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable
import org.bukkit.block.BlockFace

data class ElementFace(
    val face: BlockFace,
    val model: Model,
    val cullFace: BlockFace = face
) : Disposable by model
