package com.sksamuel.kotest.jupiter

import io.kotest.core.spec.style.FunSpec

class JupiterTest {
   @org.junit.jupiter.api.Test
   fun myTest() {
   }
}

class KotestTest : FunSpec() {
   init {
      test("foo") {}
   }
}
