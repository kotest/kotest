import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kotlin.reflect.KProperty0

/**
 * Assert that this property has a specific value. Unlike regular [shouldBe], name of the property will be
 * automatically added to the error message
 */
infix fun <T> KProperty0<T>.shouldHaveValue(value: T) {
   withClue(name) {
      this.get() shouldBe value
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
