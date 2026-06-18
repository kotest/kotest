package io.kotest.matchers.collections

import io.kotest.assertions.equals.Equality
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// Primitive array overloads
infix fun BooleanArray.shouldContain(t: Boolean): BooleanArray = apply { asList().shouldContain(t) }
infix fun BooleanArray.shouldNotContain(t: Boolean): BooleanArray = apply { asList().shouldNotContain(t) }
infix fun ByteArray.shouldContain(t: Byte): ByteArray = apply { asList().shouldContain(t) }
infix fun ByteArray.shouldNotContain(t: Byte): ByteArray = apply { asList().shouldNotContain(t) }
infix fun ShortArray.shouldContain(t: Short): ShortArray = apply { asList().shouldContain(t) }
infix fun ShortArray.shouldNotContain(t: Short): ShortArray = apply { asList().shouldNotContain(t) }
infix fun CharArray.shouldContain(t: Char): CharArray = apply { asList().shouldContain(t) }
infix fun CharArray.shouldNotContain(t: Char): CharArray = apply { asList().shouldNotContain(t) }
infix fun IntArray.shouldContain(t: Int): IntArray = apply { asList().shouldContain(t) }
infix fun IntArray.shouldNotContain(t: Int): IntArray = apply { asList().shouldNotContain(t) }
infix fun LongArray.shouldContain(t: Long): LongArray = apply { asList().shouldContain(t) }
infix fun LongArray.shouldNotContain(t: Long): LongArray = apply { asList().shouldNotContain(t) }
infix fun FloatArray.shouldContain(t: Float): FloatArray = apply { asList().shouldContain(t) }
infix fun FloatArray.shouldNotContain(t: Float): FloatArray = apply { asList().shouldNotContain(t) }
infix fun DoubleArray.shouldContain(t: Double): DoubleArray = apply { asList().shouldContain(t) }
infix fun DoubleArray.shouldNotContain(t: Double): DoubleArray = apply { asList().shouldNotContain(t) }

// Infix. These delegate directly to the `contain` matcher in kotest-assertions-core-logic so that
// the infix module depends only on logic (and not on the dot-notation assertions in standard).
infix fun <T, I : Iterable<T>> I.shouldNotContain(t: T): I = apply { toList() shouldNot contain(t, Equality.default()) }
infix fun <T> Array<T>.shouldNotContain(t: T): Array<T> = apply { asList() shouldNot contain(t, Equality.default()) }
infix fun <T, I : Iterable<T>> I.shouldContain(t: T): I = apply { toList() should contain(t, Equality.default()) }
infix fun <T> Array<T>.shouldContain(t: T): Array<T> = apply { asList() should contain(t, Equality.default()) }
