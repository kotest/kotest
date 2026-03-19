package com.sksamuel.kotest.assertions.jdk21

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import java.util.Collections

class ShouldContainExactlyTest : FunSpec() {
   init {
      test("support Java21 unmodifiableSequencedSet") {
         Collections.unmodifiableSequencedSet(linkedSetOf("a", "b", "c")).shouldContainExactly("a", "b", "c")
         Collections.unmodifiableSequencedSet(linkedSetOf("a", "b", "c")).shouldContainExactlyInAnyOrder("c", "b", "a")
      }
   }
}
