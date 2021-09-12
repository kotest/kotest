package io.kotest.common

@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalKotest

@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class DelicateKotest

/**
 * Marks a Kotest feature as only available on the JVM platform.
 */
@Retention(value = AnnotationRetention.SOURCE)
annotation class JVMOnly

/**
 * Marks a Kotest feature as soft deprecated
 */
@Retention(value = AnnotationRetention.SOURCE)
annotation class SoftDeprecated(val message: String)

