import io.kotlintest.properties.Gen
import io.kotlintest.properties.propertyAssertionError
import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.PropertyFailureInput
import io.kotlintest.properties.PropertyTesting
import io.kotlintest.properties.shrinking.Shrinker

fun <T> shrink(t: T, gen: Gen<T>, test: (T) -> Unit): T = shrink2(t, gen.shrinker(), test)

fun <T> shrink2(t: T, shrinker: Shrinker<T>?, test: (T) -> Unit): T {
  return when (shrinker) {
    null -> t
    else -> shrink(t, shrinker, test)
  }
}

fun <T> shrink(t: T, shrinker: Shrinker<T>, test: (T) -> Unit): T {
  val sb = StringBuilder()
  sb.append("Attempting to shrink failed arg ${convertValueToString(t)}\n")
  var candidate = t
  val tested = HashSet<T>()
  var count = 0
  while (true) {
    val candidates = shrinker.shrink(candidate).filterNot { tested.contains(it) }
    if (candidates.isEmpty()) {
      sb.append("Shrink result => ${convertValueToString(candidate)}\n")
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
          sb.append("Shrink #$count: ${convertValueToString(it)} pass\n")
          false
        } catch (t: Throwable) {
          sb.append("Shrink #$count: ${convertValueToString(it)} fail\n")
          true
        }
      }
      if (next == null) {
        sb.append("Shrink result => ${convertValueToString(candidate)}\n")
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
