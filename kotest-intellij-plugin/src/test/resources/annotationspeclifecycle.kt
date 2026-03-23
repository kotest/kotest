package com.sksamuel.kotest.specs

import io.kotest.core.spec.style.AnnotationSpec

class AnnotationSpecLifecycleExample : AnnotationSpec() {

   @BeforeEach
   fun beforeEachTest() {
   }

   @AfterEach
   fun afterEachTest() {
   }

   @BeforeAll
   fun beforeAllTests() {
   }

   @AfterAll
   fun afterAllTests() {
   }

   @Test
   fun myTest() {
   }

   @Ignore
   fun ignoredTest() {
   }
}
