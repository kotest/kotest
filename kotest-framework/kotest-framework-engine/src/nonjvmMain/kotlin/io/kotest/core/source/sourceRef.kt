package io.kotest.core.source

/**
 * Returns a [SourceRef] for the current execution point.
 */
internal actual fun sourceRef(): SourceRef = SourceRef.None
