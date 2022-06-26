import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class TestSpec : ShouldSpec({
   should("be able to do arithmetic") {
      1 + 1 shouldBe 2
   }
})
