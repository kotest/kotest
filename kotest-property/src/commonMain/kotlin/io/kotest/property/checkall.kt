package io.kotest.property

// todo have these methods delegate to one that uses PropTestConfig
fun checkAll(iterations: Int, seed: Long, test: PropertyContext.() -> Unit): PropertyContext {
   val context = PropertyContext(RandomSource.seeded(seed))
   repeat(iterations) {
      try {
         context.test()
      } catch (t: Throwable) {
         // shrink here then throw
      } finally {
         context.reset()
      }
   }
   return context
}

// todo have these methods delegate to one that uses PropTestConfig
fun checkAll(iterations: Int, test: PropertyContext.() -> Unit): PropertyContext {
   val context = PropertyContext(RandomSource.default())
   repeat(iterations) {
      try {
         context.test()
      } catch (t: Throwable) {
         // shrink here then throw
      } finally {
         context.reset()
      }
      context.reset()
   }
   return context
}
