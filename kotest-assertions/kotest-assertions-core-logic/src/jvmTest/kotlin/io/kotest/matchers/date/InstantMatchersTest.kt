import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.date.shouldNotBeAfter
import io.kotest.matchers.date.shouldNotBeBefore
import io.kotest.matchers.shouldBe
import java.time.Instant

class InstantMatchersTest : StringSpec( {
   "0 shouldBeBefore 1 should succeed" {
      Instant.ofEpochSecond(0) shouldBeBefore Instant.ofEpochSecond(1)
   }

   "1 shouldBeBefore 0 should throw with correct message" {
      shouldThrow<AssertionError> {
         Instant.ofEpochSecond(1) shouldBeBefore Instant.ofEpochSecond(0)
      }.message shouldBe "Expected 1970-01-01T00:00:01Z to be before 1970-01-01T00:00:00Z, but it's not."
   }

   "1 shouldNotBeBefore 0 should succeed" {
      Instant.ofEpochSecond(1) shouldNotBeBefore Instant.ofEpochSecond(0)
   }

   "0 shouldNotBeBefore 1 should throw with correct message" {
      shouldThrow<AssertionError> {
         Instant.ofEpochSecond(0) shouldNotBeBefore Instant.ofEpochSecond(1)
      }.message shouldBe "1970-01-01T00:00:00Z is not expected to be before 1970-01-01T00:00:01Z."
   }

   "1 shouldBeAfter 0 should succeed" {
      Instant.ofEpochSecond(1) shouldBeAfter Instant.ofEpochSecond(0)
   }

   "0 shouldBeAfter 1 should throw with correct message" {
      shouldThrow<AssertionError> {
         Instant.ofEpochSecond(0) shouldBeAfter Instant.ofEpochSecond(1)
      }.message shouldBe "Expected 1970-01-01T00:00:00Z to be after 1970-01-01T00:00:01Z, but it's not."
   }

   "0 shouldNotBeAfter 1 should succeed" {
      Instant.ofEpochSecond(0) shouldNotBeAfter Instant.ofEpochSecond(1)
   }

   "1 shouldNotBeAfter 0 should throw with correct message" {
      shouldThrow<AssertionError> {
         Instant.ofEpochSecond(1) shouldNotBeAfter Instant.ofEpochSecond(0)
      }.message shouldBe "1970-01-01T00:00:01Z is not expected to be after 1970-01-01T00:00:00Z"
   }
})
