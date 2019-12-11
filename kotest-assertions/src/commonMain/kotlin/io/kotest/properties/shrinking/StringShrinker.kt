package io.kotest.properties.shrinking

class StringShrinker(private val minSize: Int, private val padChar: Char) : Shrinker<String> {

   init {
      require(minSize >= 0) { "minSize must be >= 0" }
   }

   override fun shrink(failure: String): List<String> {
      return if (failure.length <= minSize) {
         emptyList()
      } else {
         val overLen  = failure.length - minSize
         val over     = failure.takeLast(overLen)
         val left     = over.take(overLen / 2 + overLen % 2)
         val right    = over.takeLast(overLen / 2)
         val minFail  = failure.take(minSize)
         val l = listOf("", left, left.padEnd(overLen, padChar), right, right.padStart(overLen, padChar))
         generateSequence { minFail }.asIterable().zip(l) { a, b -> a + b }
      }
   }
}
