package datatest

/**
 *  Indicates that a type is stable type. If a type is annotated with this annotation, kotest will
 *  use toString method of type for creating the test name when that type is used in data test.
 *
 *  Note: If you want to provide a custom implementation of test name generation other than what
 *  toString gives, you will need to extend [WithDataTestName] interface.
 * */
@Target(AnnotationTarget.CLASS)
annotation class IsStableType
