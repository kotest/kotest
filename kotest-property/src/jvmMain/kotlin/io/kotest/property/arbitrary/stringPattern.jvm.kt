// Preserves the JVM facade class name from the previous file `stringsjvm.kt`,
// so existing Java callers and pre-compiled Kotlin clients keep linking. Do not
// remove or rename without an accompanying api dump update.
@file:JvmName("StringsjvmKt")

package io.kotest.property.arbitrary

import com.github.curiousoddman.rgxgen.RgxGen
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import java.util.Random

/**
 * Generate strings that match the given pattern.
 *
 * Backed by [RgxGen](https://github.com/curious-odd-man/RgxGen), which is
 * JVM-only. Prefer the multiplatform [Arb.Companion.pattern] for new code; this
 * function will be removed in a future release together with the `rgxgen`
 * dependency.
 */
@Deprecated(
   message = "Replaced by Arb.pattern, which works on every Kotest target.",
   replaceWith = ReplaceWith("Arb.pattern(pattern)", "io.kotest.property.arbitrary.pattern"),
   level = DeprecationLevel.WARNING,
)
fun Arb.Companion.stringPattern(pattern: String): Arb<String> = object : Arb<String>() {

   val rgxgen = RgxGen.parse(pattern)

   override fun edgecase(rs: RandomSource): Sample<String>? = null
   override fun sample(rs: RandomSource): Sample<String> = sampleStringPattern(rs)

   private fun sampleStringPattern(rs: RandomSource): Sample<String> = synchronized(this) {
      val value = rgxgen.generate(Random(rs.random.nextLong()))
      Sample(value)
   }
}
