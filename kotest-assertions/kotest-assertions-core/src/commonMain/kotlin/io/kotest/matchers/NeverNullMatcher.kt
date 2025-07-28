package io.kotest.matchers

/**
 * A [io.kotest.matchers.Matcher] that asserts that the value is not `null` before performing the test.
 *
 * The matcher returned by [invert] will _also_ assert that the value is not `null`. Use this for matchers that
 * should fail on `null` values, whether called with `should` or `shouldNot`.
 */
class NeverNullMatcher<T : Any?>(
   private val next: Matcher<T>
) : Matcher<T?> {
   override fun test(value: T?): MatcherResult =
      when (value) {
         null -> MatcherResult(false, { "Expecting actual not to be null" }, { "" })
         else -> next.test(value)
      }

   override fun invert(): Matcher<T?> =
      // invert the next matcher, but not the null check
      NeverNullMatcher(next.invert())
}

fun <T : Any> neverNullMatcher(t: (T) -> MatcherResult): Matcher<T?> =
   NeverNullMatcher(
      Matcher { t(it) }
   )
