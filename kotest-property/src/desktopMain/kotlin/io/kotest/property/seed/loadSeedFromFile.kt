package io.kotest.property.seed

import io.kotest.common.DescriptorPath

internal actual fun readSeed(path: DescriptorPath): Long? = null
internal actual fun writeSeed(path: DescriptorPath, seed: Long) {}
internal actual fun clearSeed(path: DescriptorPath) {}
