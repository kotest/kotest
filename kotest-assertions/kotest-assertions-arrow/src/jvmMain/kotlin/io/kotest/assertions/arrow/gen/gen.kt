//package io.kotest.assertions.arrow.gen
//
//import arrow.core.Tuple2
//import io.kotest.properties.Gen
//
//fun <A, B, T> genT(fn: Tuple2<A, B>): Gen<T> = object : Gen<T> {
//
//  init {
//    println(fn::class.typeParameters)
//  }
//
//  override fun constants(): Iterable<T> = emptyList()
//  override fun random(): Sequence<T> =
//      generateSequence { TODO() }
//}