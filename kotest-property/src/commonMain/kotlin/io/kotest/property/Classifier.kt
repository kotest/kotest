package io.kotest.property

/**
 * A [Classifier] is a function that can be provided by a [Gen] to automatically classify values.
 */
fun interface Classifier<A> {
   fun classify(value: A): String?
}
