package io.kotest.property.arbitrary

import com.mifmif.common.regex.Generex
import io.kotest.property.Arb
import kotlin.random.asJavaRandom

/**
 * Generate strings that match the given pattern.
 *
 * The returned arb uses the [Generex](https://github.com/mifmif/Generex) library to generate strings. Generex
 * supports a very restricted subset of regular expression constructs.
 */
fun Arb.Companion.stringPattern(pattern: String) = arbitrary {
   val generex = Generex(pattern, it.random.asJavaRandom())
   generex.random()
}
