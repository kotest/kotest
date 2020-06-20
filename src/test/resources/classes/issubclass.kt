package classes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldNotBeBlank

abstract class AbstractSpec : FunSpec()

class IsSubclassTest : AbstractSpec() {
   init {
      test("a string cannot be blank") {
         "wibble".shouldNotBeBlank()
      }
   }
}
