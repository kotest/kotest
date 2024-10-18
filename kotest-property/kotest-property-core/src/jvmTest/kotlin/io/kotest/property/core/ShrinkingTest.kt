package io.kotest.property.core

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.checkAll

@EnabledIf(LinuxCondition::class)
class ShrinkingTest : FunSpec() {
   init {
      test("shrinking should show the exception raised by the shrunk values") {

         val stdout = captureStandardOut {
            shouldThrowAny {
               checkAll<String, String>(PropTestConfig(seed = 324236)) { a, b ->
                  (a + b).length shouldBeLessThan 4
               }
            }
         }

         stdout.shouldContain(
            """
Attempting to shrink arg ",!=kC`S:ZwJh,={`B4?@EW)n@*2g!@"KKQ\mQcJa-/_s_q}vo>yft\21]S=^z^RG,Em]CHbDQz;${'$'}6(iWqK(O4${'$'}cY,Adg@"
Shrink #1: ",!=kC`S:ZwJh,={`B4?@EW)n@*2g!@"KKQ\mQcJa-/_s_q}" fail
Shrink #2: ",!=kC`S:ZwJh,={`B4?@EW)n" fail
Shrink #3: ",!=kC`S:ZwJh" fail
Shrink #4: ",!=kC`" fail
Shrink #5: ",!=" fail
Shrink #6: ",!" fail
Shrink #7: "," fail
Shrink #8: <empty string> fail
Shrink result (after 8 shrinks) => <empty string>
            """.trim()
         )

         stdout.shouldContain(
            """
Attempting to shrink arg "<uhk)r>9"
Shrink #1: "<uhk" fail
Shrink #2: "<u" pass
Shrink #3: "hk" pass
Shrink #4: "uhk" pass
Shrink #5: "<uh" pass
Shrink #6: "<<hk" fail
Shrink #7: "<<" pass
Shrink #8: "<hk" pass
Shrink #9: "<<h" pass
Shrink #10: "<<<k" fail
Shrink #11: "<k" pass
Shrink #12: "<<k" pass
Shrink #13: "<<<" pass
Shrink #14: "<<<<" fail
Shrink result (after 14 shrinks) => "<<<<"
            """.trim()
         )
      }

      test("withEdgecases should maintain the shrinker") {
         val arb = Arb.int().withEdgecases(5, 6)

         val stdout = captureStandardOut {
            shouldThrowAny {
               checkAll(PropTestConfig(seed = 324236), arb, arb) { a, b ->
                  (a + b) shouldBeLessThan 4
               }
            }
         }

         stdout.shouldContain(
            """Attempting to shrink arg -245346456
Shrink #1: 0 fail
Shrink result (after 1 shrinks) => 0"""
         )
      }
   }
}
