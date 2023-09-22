import io.kotest.assertions.withClue
import io.kotest.matchers.EqualityMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.reflect.KProperty0

/**
 * Assert that this property has a specific value. Unlike regular [shouldBe], name of the property will be
 * automatically added to the error message
 */
infix fun <T> KProperty0<T>.shouldHaveValue(t: T) = this should haveValue(t)

fun <T> haveValue(t: T) = object : Matcher<KProperty0<T>> {
   override fun test(value: KProperty0<T>): MatcherResult {
      val actual = value.get()
      val res = runCatching {
         actual shouldBe t
      }

      return EqualityMatcherResult(
         res.isSuccess,
         actual,
         t,
         {
            val detailedMessage = res.exceptionOrNull()?.message
            "Property '${value.name}' should have value $t\n$detailedMessage"
         },
         {
            val detailedMessage = res.exceptionOrNull()?.message
            "Property '${value.name}' should not have value $t\n$detailedMessage"
         },
      )
   }
}

/**
 * Perform assertions on the value of this property.
 *
 * Name of the property will be automatically added to the error message should any failures occur within the block.
 */
inline infix fun <T> KProperty0<T>.shouldMatch(block: T.() -> Unit) {
   withClue(name) {
      block(get())
   }
}
