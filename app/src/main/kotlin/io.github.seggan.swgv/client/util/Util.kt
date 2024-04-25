package io.github.seggan.swgv.client.util

import java.util.*

fun <T> identityHashSet(): MutableSet<T> = Collections.newSetFromMap(IdentityHashMap<T, Boolean>())