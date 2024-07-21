package io.kotest.core.factory

/**
 * Returns a unique id. Defaults to UUID implementations where available.
 */
internal expect fun uniqueId(): String
