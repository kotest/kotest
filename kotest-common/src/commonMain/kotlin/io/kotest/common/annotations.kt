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
@Retention(value = AnnotationRetention.BINARY)
@Target(
   AnnotationTarget.CLASS,
   AnnotationTarget.FUNCTION,
   AnnotationTarget.PROPERTY,
   AnnotationTarget.ANNOTATION_CLASS,
   AnnotationTarget.CONSTRUCTOR,
   AnnotationTarget.PROPERTY_SETTER,
   AnnotationTarget.PROPERTY_GETTER,
   AnnotationTarget.TYPEALIAS
)
annotation class SoftDeprecated(val message: String)

