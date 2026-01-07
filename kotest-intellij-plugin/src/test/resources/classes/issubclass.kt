package classes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.plugin.intellij.AbstractSpec

abstract class AbstractSpec : FunSpec()

class IsSubclassTest : AbstractSpec() {
   init {
      test("a string cannot be blank") {
         "wibble".shouldNotBeBlank()
      }
   }
}
