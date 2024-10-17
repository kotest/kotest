package io.kotest.property

fun interface Classifier<A> {
   fun classify(value: A): String?
}
