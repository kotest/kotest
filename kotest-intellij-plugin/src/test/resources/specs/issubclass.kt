package specs

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldNotBeBlank

abstract class AbstractSpec : FunSpec()

class IsSubclassOfSpec1 : AbstractSpec() {
   init {
      test("a string cannot be blank") {
         "wibble".shouldNotBeBlank()
      }
   }
}

class IsSubclassOfSpec2 : BehaviorSpec()

class IsSubclassOfSpec3 : java.util.LinkedList<String>()

class IsSubclassOfSpec4 : java.util.LinkedList<String>()

