package io.kotest.permutations.seeds

import io.kotest.core.descriptors.DescriptorPath

internal actual fun readSeed(path: DescriptorPath): Long? = null
internal actual fun writeSeed(path: DescriptorPath, seed: Long) {}
internal actual fun clearSeed(path: DescriptorPath) {}
