package io.kotest.core

import io.kotest.core.SourceRef

/**
 * Returns a [SourceRef] for the current execution point.
 */
actual fun sourceRef(): SourceRef = SourceRef(-1, "")
