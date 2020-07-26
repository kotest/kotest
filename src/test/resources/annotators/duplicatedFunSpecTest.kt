package annotators

import io.kotest.core.spec.style.FunSpec

class DuplicatedTest : FunSpec({

   test("foo") {  }

   test("foo") {  }
})
