package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.bimap
import kotlin.random.nextUInt

/**
 * Returns an [Arb] that produces [Byte]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1, 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.byte(min: Byte = Byte.MIN_VALUE, max: Byte = Byte.MAX_VALUE): Arb<Byte> =
   arbitrary(byteArrayOf(min, -1, 0, 1, max).filter { it in min..max }.distinct(), ByteShrinker) {
      generateSequence { it.random.nextBytes(1).first() }.filter { it in min..max }.first()
   }

val ByteShrinker = IntShrinker(Byte.MIN_VALUE..Byte.MAX_VALUE).bimap({ it.toInt() }, { it.toByte() })

/**
 * Returns an [Arb] that produces positive [Byte]s from 1 to [max] (inclusive).
 * The edge cases are 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.positiveByte(max: Byte = Byte.MAX_VALUE): Arb<Byte> = byte(1, max)

/**
 * Returns an [Arb] that produces negative [Byte]s from [min] to -1 (inclusive).
 * The edge cases are [min] and -1 which are only included if they are in the provided range.
 */
fun Arb.Companion.negativeByte(min: Byte = Byte.MIN_VALUE): Arb<Byte> = byte(min, -1)

/**
 * Returns an [Arb] that produces [ByteArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.byteArray(length: Gen<Int>, content: Arb<Byte>): Arb<ByteArray> =
   toPrimitiveArray(length, content, Collection<Byte>::toByteArray)

/**
 * Returns an [Arb] that produces [UByte]s from [min] to [max] (inclusive).
 * The edge cases are [min], 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.uByte(min: UByte = UByte.MIN_VALUE, max: UByte = UByte.MAX_VALUE): Arb<UByte> =
   arbitrary(listOf(min, 1u, max).filter { it in min..max }.distinct(), UByteShrinker) {
      it.random.nextUInt(min..max).toUByte()
   }

val UByteShrinker = UIntShrinker(UByte.MIN_VALUE..UByte.MAX_VALUE).bimap({ it.toUInt() }, { it.toUByte() })

/**
 * Returns an [Arb] that produces [UByteArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
@ExperimentalUnsignedTypes
fun Arb.Companion.uByteArray(length: Gen<Int>, content: Arb<UByte>): Arb<UByteArray> =
   toPrimitiveArray(length, content, Collection<UByte>::toUByteArray)
