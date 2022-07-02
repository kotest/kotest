package io.kotest.core.descriptors

/**
 * Breaks down a [TestPath] into its constituents, so it can be used for comparing paths in a safe way
 */
internal fun Descriptor.tokenizedPath(): List<String> =
   this.path().value.split(Descriptor.TestDelimiter, Descriptor.SpecDelimiter)
