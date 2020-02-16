//package io.kotest.data
//
//import kotlin.jvm.JvmName
//
//@JvmName("forAll3receiver") inline fun <A, B, C> Table3<A, B, C>.forAll(fn: (A, B, C) -> Unit) = forAll(this, fn)
//@JvmName("forAll4receiver") inline fun <A, B, C, D> Table4<A, B, C, D>.forAll(fn: (A, B, C, D) -> Unit) = forAll(this, fn)
//@JvmName("forAll5receiver") inline fun <A, B, C, D, E> Table5<A, B, C, D, E>.forAll(fn: (A, B, C, D, E) -> Unit) = forAll(this, fn)
//@JvmName("forAll6receiver") inline fun <A, B, C, D, E, F> Table6<A, B, C, D, E, F>.forAll(fn: (A, B, C, D, E, F) -> Unit) = forAll(this, fn)
//@JvmName("forAll7receiver") inline fun <A, B, C, D, E, F, G> Table7<A, B, C, D, E, F, G>.forAll(fn: (A, B, C, D, E, F, G) -> Unit) = forAll(this, fn)
//@JvmName("forAll8receiver") inline fun <A, B, C, D, E, F, G, H> Table8<A, B, C, D, E, F, G, H>.forAll(fn: (A, B, C, D, E, F, G, H) -> Unit) = forAll(this, fn)
//@JvmName("forAll9receiver") inline fun <A, B, C, D, E, F, G, H, I> Table9<A, B, C, D, E, F, G, H, I>.forAll(fn: (A, B, C, D, E, F, G, H, I) -> Unit) = forAll(this, fn)
//@JvmName("forAll10receiver") inline fun <A, B, C, D, E, F, G, H, I, J> Table10<A, B, C, D, E, F, G, H, I, J>.forAll(fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) = forAll(this, fn)
//@JvmName("forAll11receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K> Table11<A, B, C, D, E, F, G, H, I, J, K>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) = forAll(this, fn)
//@JvmName("forAll12receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Table12<A, B, C, D, E, F, G, H, I, J, K, L>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> Unit) = forAll(this, fn)
//@JvmName("forAll13receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) = forAll(this, fn)
//@JvmName("forAll14receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Table14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit) = forAll(this, fn)
//@JvmName("forAll15receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Table15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit) = forAll(this, fn)
//@JvmName("forAll16receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Table16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit) = forAll(this, fn)
//@JvmName("forAll17receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Table17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit) = forAll(this, fn)
//@JvmName("forAll18receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Table18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> Unit) = forAll(this, fn)
//@JvmName("forAll19receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) = forAll(this, fn)
//@JvmName("forAll20receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Table20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> Unit) = forAll(this, fn)
//@JvmName("forAll21receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Table21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> Unit) = forAll(this, fn)
//@JvmName("forAll22receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Table22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> Unit) = forAll(this, fn)
