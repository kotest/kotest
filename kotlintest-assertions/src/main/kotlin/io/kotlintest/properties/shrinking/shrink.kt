import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.PropertyFailureInput
import io.kotlintest.properties.PropertyTesting
import io.kotlintest.properties.propertyAssertionError
import io.kotlintest.properties.shrinking.Shrinker

inline fun <A> shrinkInputs(
        a: A,
        shrinkerA: Shrinker<A>?,
        context: PropertyContext, assertionError: AssertionError,
        fn: PropertyContext.(a: A) -> Unit
) {
  val smallestA = shrink(a, shrinkerA) { context.fn(it) }
  throwSmallestInputError(a, smallestA, assertionError, context)
}

@PublishedApi
internal fun <A> throwSmallestInputError(
        a: A,
        smallestA: A,
        assertionError: AssertionError, context: PropertyContext
) {
  val inputs = listOf(
          PropertyFailureInput(a, smallestA)
  )
  throw propertyAssertionError(assertionError, context.attempts(), inputs)
}

inline fun <A, B> shrinkInputs(
        a: A, b: B,
        shrinkerA: Shrinker<A>?, shrinkerB: Shrinker<B>?,
        context: PropertyContext, assertionError: AssertionError,
        fn: PropertyContext.(a: A, b: B) -> Unit
) {
  val smallestA = shrink(a, shrinkerA) { context.fn(it, b) }
  val smallestB = shrink(b, shrinkerB) { context.fn(smallestA, it) }
  throwSmallestInputError(a, b, smallestA, smallestB, assertionError, context)
}

@PublishedApi
internal fun <A, B> throwSmallestInputError(
        a: A, b: B,
        smallestA: A, smallestB: B,
        assertionError: AssertionError, context: PropertyContext
) {
  val inputs = listOf(
          PropertyFailureInput(a, smallestA),
          PropertyFailureInput(b, smallestB)
          )
  throw propertyAssertionError(assertionError, context.attempts(), inputs)
}

inline fun <A, B, C> shrinkInputs(
        a: A, b: B, c: C,
        shrinkerA: Shrinker<A>?, shrinkerB: Shrinker<B>?, shrinkerC: Shrinker<C>?,
        context: PropertyContext, assertionError: AssertionError,
        fn: PropertyContext.(a: A, b: B, c: C) -> Unit
) {
  val smallestA = shrink(a, shrinkerA) { context.fn(it, b, c) }
  val smallestB = shrink(b, shrinkerB) { context.fn(smallestA, it, c) }
  val smallestC = shrink(c, shrinkerC) { context.fn(smallestA, smallestB, it) }
  throwSmallestInputError(a, b, c, smallestA, smallestB, smallestC, assertionError, context)
}

@PublishedApi
internal fun <A, B, C> throwSmallestInputError(
        a: A, b: B, c: C,
        smallestA: A, smallestB: B, smallestC: C,
        assertionError: AssertionError, context: PropertyContext
) {
  val inputs = listOf(
          PropertyFailureInput(a, smallestA),
          PropertyFailureInput(b, smallestB),
          PropertyFailureInput(c, smallestC)
  )
  throw propertyAssertionError(assertionError, context.attempts(), inputs)
}

inline fun <A, B, C, D> shrinkInputs(
        a: A, b: B, c: C, d: D,
        shrinkerA: Shrinker<A>?, shrinkerB: Shrinker<B>?, shrinkerC: Shrinker<C>?, shrinkerD: Shrinker<D>?,
        context: PropertyContext, assertionError: AssertionError,
        fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit
) {
  val smallestA = shrink(a, shrinkerA) { context.fn(it, b, c, d) }
  val smallestB = shrink(b, shrinkerB) { context.fn(smallestA, it, c, d) }
  val smallestC = shrink(c, shrinkerC) { context.fn(smallestA, smallestB, it, d) }
  val smallestD = shrink(d, shrinkerD) { context.fn(smallestA, smallestB, smallestC, it) }
  throwSmallestInputError(a, b, c, d, smallestA, smallestB, smallestC, smallestD, assertionError, context)
}

@PublishedApi
internal fun <A, B, C, D> throwSmallestInputError(
        a: A, b: B, c: C, d: D,
        smallestA: A, smallestB: B, smallestC: C, smallestD: D,
        assertionError: AssertionError, context: PropertyContext
) {
  val inputs = listOf(
          PropertyFailureInput(a, smallestA),
          PropertyFailureInput(b, smallestB),
          PropertyFailureInput(c, smallestC),
          PropertyFailureInput(d, smallestD)
  )
  throw propertyAssertionError(assertionError, context.attempts(), inputs)
}

inline fun <A, B, C, D, E> shrinkInputs(
        a: A, b: B, c: C, d: D, e: E,
        shrinkerA: Shrinker<A>?, shrinkerB: Shrinker<B>?, shrinkerC: Shrinker<C>?, shrinkerD: Shrinker<D>?, shrinkerE: Shrinker<E>?,
        context: PropertyContext, assertionError: AssertionError,
        fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit
) {
  val smallestA = shrink(a, shrinkerA) { context.fn(it, b, c, d, e) }
  val smallestB = shrink(b, shrinkerB) { context.fn(smallestA, it, c, d, e) }
  val smallestC = shrink(c, shrinkerC) { context.fn(smallestA, smallestB, it, d, e) }
  val smallestD = shrink(d, shrinkerD) { context.fn(smallestA, smallestB, smallestC, it, e) }
  val smallestE = shrink(e, shrinkerE) { context.fn(smallestA, smallestB, smallestC, smallestD, it) }
  
  throwSmallestInputError(a, smallestA, b, smallestB, c, smallestC, d, smallestD, e, smallestE, assertionError, context)
}

@PublishedApi
internal fun <A, B, C, D, E, F> throwSmallestInputError(
        a: A, b: B, c: C, d: D, e: E, f: F,
        smallestA: A, smallestB: B, smallestC: C, smallestD: D, smallestE: E, smallestF: F,
        assertionError: AssertionError, context: PropertyContext
) {
  val inputs = listOf(
          PropertyFailureInput(a, smallestA),
          PropertyFailureInput(b, smallestB),
          PropertyFailureInput(c, smallestC),
          PropertyFailureInput(d, smallestD),
          PropertyFailureInput(e, smallestE),
          PropertyFailureInput(f, smallestF)
  )
  throw propertyAssertionError(assertionError, context.attempts(), inputs)
}

inline fun <A, B, C, D, E, F> shrinkInputs(
        a: A, b: B, c: C, d: D, e: E, f: F,
        shrinkerA: Shrinker<A>?, shrinkerB: Shrinker<B>?, shrinkerC: Shrinker<C>?, shrinkerD: Shrinker<D>?, shrinkerE: Shrinker<E>?, shrinkerF: Shrinker<F>?,
        context: PropertyContext, assertionError: AssertionError,
        fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit
) {
  val smallestA = shrink(a, shrinkerA) { context.fn(it, b, c, d, e, f) }
  val smallestB = shrink(b, shrinkerB) { context.fn(smallestA, it, c, d, e, f) }
  val smallestC = shrink(c, shrinkerC) { context.fn(smallestA, smallestB, it, d, e, f) }
  val smallestD = shrink(d, shrinkerD) { context.fn(smallestA, smallestB, smallestC, it, e, f) }
  val smallestE = shrink(e, shrinkerE) { context.fn(smallestA, smallestB, smallestC, smallestD, it, f) }
  val smallestF = shrink(f, shrinkerF) { context.fn(smallestA, smallestB, smallestC, smallestD, smallestE, it) }
  
  throwSmallestInputError(a, smallestA, b, smallestB, c, smallestC, d, smallestD, e, smallestE, f, smallestF, assertionError, context)
}

@PublishedApi
internal fun <A, B, C, D, E> throwSmallestInputError(
        a: A, b: B, c: C, d: D, e: E,
        smallestA: A, smallestB: B, smallestC: C, smallestD: D, smallestE: E,
        assertionError: AssertionError, context: PropertyContext
) {
  val inputs = listOf(
          PropertyFailureInput(a, smallestA),
          PropertyFailureInput(b, smallestB),
          PropertyFailureInput(c, smallestC),
          PropertyFailureInput(d, smallestD),
          PropertyFailureInput(e, smallestE)
  )
  throw propertyAssertionError(assertionError, context.attempts(), inputs)
}

inline fun <T> shrink(t: T, shrinker: Shrinker<T>?, test: (T) -> Unit): T {
  return when (shrinker) {
    null -> t
    else -> findSmallestFailureValue(t, shrinker, test)
  }
}

inline fun <T> findSmallestFailureValue(value: T, shrinker: Shrinker<T>, test: (T) -> Unit): T {
  return ShrinkingProcess(value, shrinker).shrink(test)
}

@PublishedApi
internal class ShrinkingProcess<T>(
        startingValue: T,
        @PublishedApi internal val shrinker: Shrinker<T>
) {
  private val stringLog = StringBuilder("Attempting to shrink failed arg ${convertValueToString(startingValue)}\n")
  
  @PublishedApi internal var currentCandidate = startingValue
  @PublishedApi internal val testedValues = HashSet<T>()
  
  @PublishedApi
  internal val amountTested: Int
    get() = testedValues.size
  
  inline fun shrink(test: (T) -> Unit): T {
    while (true) {
      val candidates = shrinker.shrink(currentCandidate).filterNot { it in testedValues }
      if (candidates.isEmpty()) {
        logShrinkResult()
        return currentCandidate
      } else {
        val nextCandidate = candidates.findFailingCandidate(test)
        
        if (nextCandidate == null) {
          logShrinkResult()
          return currentCandidate
        }
        
        currentCandidate = nextCandidate
      }
    }
  }
  
  @PublishedApi
  internal fun logShrinkResult() {
    stringLog.append("Shrink result => ${convertValueToString(currentCandidate)}\n")
    if (PropertyTesting.shouldPrintShrinkSteps) {
      println(stringLog)
    }
  }
  
  inline fun List<T>.findFailingCandidate(test: (T) -> Unit): T? {
    forEach {
      testedValues.add(it)
      try {
        test(it)
        registerPassedTest(it)
      } catch (_: Throwable) {
        registerFailedTest(it)
        return it
      }
    }
    
    return null
  }
  
  @PublishedApi
  internal fun registerPassedTest(it: T) {
    stringLog.append("Shrink #$amountTested: ${convertValueToString(it)} pass\n")
  }
  
  @PublishedApi
  internal fun registerFailedTest(it: T) {
    stringLog.append("Shrink #$amountTested: ${convertValueToString(it)} fail\n")
  }
}