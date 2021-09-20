package io.kotest.property

fun checkAll(iterations: Int, seed: Long, test: PropertyContext.() -> Unit): PropertyContext {
   val context = PropertyContext(RandomSource.seeded(seed))
   repeat(iterations) {
      context.test()
      context.reset()
   }
   return context
}

fun checkAll(iterations: Int, test: PropertyContext.() -> Unit): PropertyContext {
   val context = PropertyContext(RandomSource.default())
   repeat(iterations) {
      context.test()
      context.reset()
   }
   return context
}
