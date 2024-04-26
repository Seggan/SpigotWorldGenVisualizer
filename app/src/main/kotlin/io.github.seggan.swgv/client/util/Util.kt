package io.github.seggan.swgv.client.util

import java.util.*

fun <T> identityHashSet(): MutableSet<T> = Collections.newSetFromMap(IdentityHashMap<T, Boolean>())

fun distanceSquared(x1: Int, y1: Int, x2: Int, y2: Int): Int {
    val dx = x1 - x2
    val dy = y1 - y2
    return dx * dx + dy * dy
}