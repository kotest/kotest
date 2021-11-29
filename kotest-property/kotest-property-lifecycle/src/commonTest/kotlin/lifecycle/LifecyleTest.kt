package lifecycle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.lifecycle.afterProperty
import io.kotest.property.lifecycle.beforeProperty

class LifecyleTest : FunSpec() {
   init {

      var beforeCounter = 0
      var afterCounter = 0

      beforeProperty {
         beforeCounter++
      }

      afterProperty {
         afterCounter++
      }

      beforeProperty {
         // test that we can support multiple
         beforeCounter++
      }

      afterProperty {
         // test that we can support multiple
         afterCounter++
      }

      test("property test") {
         checkAll<String, String>(iterations = 31) { a, b -> a + b shouldBe "$a$b" }
      }

      afterProject {
         beforeCounter shouldBe 62
         afterCounter shouldBe 62
      }
   }


}
