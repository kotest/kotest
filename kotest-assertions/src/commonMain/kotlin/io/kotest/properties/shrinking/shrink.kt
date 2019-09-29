package io.kotest.properties.shrinking

import io.kotest.assertions.show.show
import io.kotest.properties.Gen
import io.kotest.properties.PropertyContext
import io.kotest.properties.PropertyFailureInput
import io.kotest.properties.PropertyTesting
import io.kotest.properties.propertyAssertionError

fun <T> shrink(t: T, gen: Gen<T>, test: (T) -> Unit): T = shrink2(t, gen.shrinker(), test)

fun <T> shrink2(t: T, shrinker: Shrinker<T>?, test: (T) -> Unit): T {
  return when (shrinker) {
    null -> t
    else -> shrink(t, shrinker, test)
  }
}

fun <T> shrink(t: T, shrinker: Shrinker<T>, test: (T) -> Unit): T {
  val sb = StringBuilder()
  sb.append("Attempting to io.kotest.properties.shrinking.shrink failed arg ${t.show()}\n")
  var candidate = t
  val tested = HashSet<T>()
  var count = 0
  while (true) {
    val candidates = shrinker.shrink(candidate).filterNot { tested.contains(it) }
    if (candidates.isEmpty()) {
      sb.append("Shrink result => ${candidate.show()}\n")
      if (PropertyTesting.shouldPrintShrinkSteps) {
        println(sb)
      }
      return candidate
    } else {
      val next = candidates.firstOrNull {
        tested.add(it)
        count++
        try {
          test(it)
          sb.append("Shrink #$count: ${it.show()} pass\n")
          false
        } catch (t: Throwable) {
          sb.append("Shrink #$count: ${it.show()} fail\n")
          true
        }
      }
      if (next == null) {
        sb.append("Shrink result => ${candidate.show()}\n")
        if (PropertyTesting.shouldPrintShrinkSteps) {
          println(sb)
        }
        return candidate
      } else {
        candidate = next
      }
    }
  }

}

fun <A, B, C, D> shrinkInputs(a: A,
                              b: B,
                              c: C,
                              d: D,
                              gena: Gen<A>,
                              genb: Gen<B>,
                              genc: Gen<C>,
                              gend: Gen<D>,
                              context: PropertyContext,
                              fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit,
                              e: AssertionError) {
  val smallestA = shrink(a, gena) { context.fn(it, b, c, d) }
  val smallestB = shrink(b, genb) { context.fn(smallestA, it, c, d) }
  val smallestC = shrink(c, genc) { context.fn(smallestA, smallestB, it, d) }
  val smallestD = shrink(d, gend) { context.fn(smallestA, smallestB, smallestC, it) }
  val inputs = listOf(
      PropertyFailureInput<A>(a, smallestA),
      PropertyFailureInput<B>(b, smallestB),
      PropertyFailureInput<C>(c, smallestC),
      PropertyFailureInput<D>(d, smallestD)
  )
  throw propertyAssertionError(e, context.attempts(), inputs)
}
