package io.kotest.property

fun checkAll(iterations: Int, seed: Long, test: PropertyContext.() -> Unit) {
   val context = PropertyContext(RandomSource.seeded(seed))
   repeat(iterations) {
      context.test()
      context.reset()
   }
}

fun checkAll(iterations: Int, test: PropertyContext.() -> Unit) {
   val context = PropertyContext(RandomSource.default())
   repeat(iterations) {
      context.test()
      context.reset()
   }
}
