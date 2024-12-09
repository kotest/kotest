package io.kotest.engine.stable

/**
 *  Indicates that a type is stable type.
 *
 *  A stable type is one where the toString() representation of the type is consistent across runs
 *  and can be relied on to generate a safe test name or test identifer.
 * */
@Target(AnnotationTarget.CLASS)
annotation class IsStableType
