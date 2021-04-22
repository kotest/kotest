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
               checkAll<String, String>(PropTestConfig(seed = 12312313)) { a, b ->
                  (a + b).length shouldBeLessThan 4
               }
            }
         }

         stdout.shouldContain(
            """
Attempting to shrink arg ""aaVTT${"$"}H<<3&rYlvW==(>]8Sga"
Shrink #1: ""aaVTT${"$"}H<<3&r" fail
Shrink #2: ""aaVTT${'$'}" fail
Shrink #3: ""aaV" fail
Shrink #4: ""a" fail
Shrink #5: ""${'"'} fail
Shrink #6: <empty string> fail
Shrink result (after 6 shrinks) => <empty string>
            """.trim()
         )

         stdout.shouldContain(
            """
Attempting to shrink arg "Z7XHs_\&5YUAua4^Eq>m${"$"}w:-\EtY\3b<${'$'}<[WnDk,e'T9I3?1+Rvx;")63cwl!`"
Shrink #1: "Z7XHs_\&5YUAua4^Eq>m${"$"}w:-\EtY\3b" fail
Shrink #2: "Z7XHs_\&5YUAua4^" fail
Shrink #3: "Z7XHs_\&" fail
Shrink #4: "Z7XH" fail
Shrink #5: "Z7" pass
Shrink #6: "XH" pass
Shrink #7: "7XH" pass
Shrink #8: "Z7X" pass
Shrink #9: "a7XH" fail
Shrink #10: "a7" pass
Shrink #11: "a7X" pass
Shrink #12: "aaXH" fail
Shrink #13: "aa" pass
Shrink #14: "aXH" pass
Shrink #15: "aaX" pass
Shrink #16: "aaaH" fail
Shrink #17: "aH" pass
Shrink #18: "aaH" pass
Shrink #19: "aaa" pass
Shrink #20: "aaaa" fail
Shrink result (after 20 shrinks) => "aaaa"
            """.trim()
         )
      }
   }
}
