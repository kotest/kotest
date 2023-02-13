package io.kotest.common

@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalKotest

/**
 * Any feature annotated with [DelicateKotest] is an advanced feature.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class DelicateKotest

/**
 * An internal Kotest feature that is public for operational reasons but should not be used by end users.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class KotestInternal


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
