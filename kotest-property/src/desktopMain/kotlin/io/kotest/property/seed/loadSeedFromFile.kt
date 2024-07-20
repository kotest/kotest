package io.kotest.property.seed

import io.kotest.common.TestPath

internal actual fun readSeed(path: TestPath): Long? = null
internal actual fun writeSeed(path: TestPath, seed: Long) {}
internal actual fun clearSeed(path: TestPath) {}
internal actual suspend fun cleanUpSeedFiles() {}
