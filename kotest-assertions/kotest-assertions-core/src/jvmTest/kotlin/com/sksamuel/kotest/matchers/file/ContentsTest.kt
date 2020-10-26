package com.sksamuel.kotest.matchers.file

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.file.shouldHaveSameContentAs
import io.kotest.matchers.throwable.shouldHaveMessage
import java.nio.file.Files

class ContentsTest : FunSpec() {
   init {
      test("file contents match") {
         val a = Files.createTempFile("a", "txt")
         val b = Files.createTempFile("a", "txt")
         Files.write(a, "foo\nbar\nbaz".lines())
         Files.write(b, "foo\nbar\nbaz".lines())
         a.toFile().shouldHaveSameContentAs(b.toFile())
      }
      test("file contents differ") {
         val a = Files.createTempFile("a", "txt")
         val b = Files.createTempFile("a", "txt")
         Files.write(a, "foo\nwoo\nbaz".lines())
         Files.write(b, "foo\nbar\nbaz".lines())
         shouldThrowAny {
            a.toFile().shouldHaveSameContentAs(b.toFile())
         }.shouldHaveMessage("""Files $a and $b should have the same content.
Instead they differ at line 2:
+ woo
- bar""")
      }
      test("file contents match but one has more lines") {
         val a = Files.createTempFile("a", "txt")
         val b = Files.createTempFile("a", "txt")
         Files.write(a, "foo\nbar\nbaz".lines())
         Files.write(b, "foo\nbar".lines())
         shouldThrowAny {
            a.toFile().shouldHaveSameContentAs(b.toFile())
         }.shouldHaveMessage("""Files $a and $b should have the same content.
File $a has additional lines, starting at line 3: baz""")
      }
   }
}
