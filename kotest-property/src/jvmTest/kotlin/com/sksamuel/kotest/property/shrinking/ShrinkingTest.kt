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

         // This is work-around for using $ in a multiline string literal
         val pk = "\$pk"
         stdout.shouldContain(
            """
Attempting to shrink arg "X3f),#-'S]i~RgC'Ke_\^iWT2XgQ*L?{f$pk]Cfrj.X(r;3FWg"
Shrink #1: "X3f),#-'S]i~RgC'Ke_\^iWT2" fail
Shrink #2: "X3f),#-'S]i~R" fail
Shrink #3: "X3f),#-" fail
Shrink #4: "X3f)" fail
Shrink #5: "X3" fail
Shrink #6: "X" fail
Shrink #7: <empty string> fail
Shrink result (after 7 shrinks) => <empty string>
            """.trim()
         )

         stdout.shouldContain(
            """
Attempting to shrink arg "O#w>"l&~*9H0U*an#I&[ _On\"fYS>&4e~4#P2V'[tna;v@8#2?89CWkhnU"
Shrink #1: "O#w>"l&~*9H0U*an#I&[ _On\"fYS>" fail
Shrink #2: "O#w>"l&~*9H0U*a" fail
Shrink #3: "O#w>"l&~" fail
Shrink #4: "O#w>" fail
Shrink #5: "O#" pass
Shrink #6: "w>" pass
Shrink #7: "#w>" pass
Shrink #8: "O#w" pass
Shrink #9: "OOw>" fail
Shrink #10: "OO" pass
Shrink #11: "Ow>" pass
Shrink #12: "OOw" pass
Shrink #13: "OOO>" fail
Shrink #14: "O>" pass
Shrink #15: "OO>" pass
Shrink #16: "OOO" pass
Shrink #17: "OOOO" fail
Shrink result (after 17 shrinks) => "OOOO"
            """.trim()
         )
      }
   }
}
