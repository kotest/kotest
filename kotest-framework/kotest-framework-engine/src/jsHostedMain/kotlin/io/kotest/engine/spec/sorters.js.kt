package io.kotest.engine.spec

/**
 * Note: No file system so this sort order is a no-op
 */
actual val FailureFirstSorter: SpecSorter = NoopSpecSorter

/**
 * Note: Runtime annotations are not supported on Native or JS so on those platforms
 * this sort order is a no-op.
 */
actual val AnnotatedSpecSorter: SpecSorter = NoopSpecSorter
