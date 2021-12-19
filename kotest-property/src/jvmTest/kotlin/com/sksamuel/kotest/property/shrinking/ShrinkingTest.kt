package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll

class ShrinkingTest : FunSpec() {
   init {
      test("shrinking should show the exception raised by the shrunk values") {

         val stdout = captureStandardOut {
            shouldThrowAny {
               checkAll<String, String>(PropTestConfig(seed = 324234)) { a, b ->
                  (a + b).length shouldBeLessThan 4
               }
            }
         }

         stdout.shouldContain(
            """
Attempting to shrink arg "K\#a")?7'9G/B'#[X:8+uN"le*m]^^9iOoD2CQ3!ZN6XT)?,ii"
Shrink #1: "K\#a")?7'9G/B'#[X:8+uN"le" fail
Shrink #2: "K\#a")?7'9G/B" fail
Shrink #3: "K\#a")?" fail
Shrink #4: "K\#a" fail
Shrink #5: "K\" fail
Shrink #6: "K" fail
Shrink #7: <empty string> fail
Shrink result (after 7 shrinks) => <empty string>
            """.trim()
         )

         stdout.shouldContain(
            """
Attempting to shrink arg "2K6iUxNE)Dt0?dpR\\2 jt`@7K(cIint'OKqR(,gm(5EInW(?_o.R Q2_t%"
Shrink #1: "2K6iUxNE)Dt0?dpR\\2 jt`@7K(cIi" fail
Shrink #2: "2K6iUxNE)Dt0?dp" fail
Shrink #3: "2K6iUxNE" fail
Shrink #4: "2K6i" fail
Shrink #5: "2K" pass
Shrink #6: "6i" pass
Shrink #7: "K6i" pass
Shrink #8: "2K6" pass
Shrink #9: "226i" fail
Shrink #10: "22" pass
Shrink #11: "26i" pass
Shrink #12: "226" pass
Shrink #13: "222i" fail
Shrink #14: "2i" pass
Shrink #15: "22i" pass
Shrink #16: "222" pass
Shrink #17: "2222" fail
Shrink result (after 17 shrinks) => "2222"
            """.trim()
         )
      }
   }
}
