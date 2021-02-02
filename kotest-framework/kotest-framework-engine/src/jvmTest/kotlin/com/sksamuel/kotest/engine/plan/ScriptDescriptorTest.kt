package com.sksamuel.kotest.engine.plan

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScriptDescriptorTest : FunSpec() {
   init {
      test("classname should be set when generating a script name") {
         Descriptor.fromScriptClass(ScriptDescriptorTest::class).classname shouldBe "com.sksamuel.kotest.engine.plan.ScriptDescriptorTest"
      }
      test("name should be set when generating a script name") {
         Descriptor.fromScriptClass(ScriptDescriptorTest::class).name.value shouldBe "com.sksamuel.kotest.engine.plan.ScriptDescriptorTest"
      }
      test("test path should be set when generating a script name") {
         Descriptor.fromScriptClass(ScriptDescriptorTest::class).testPath().value shouldBe "kotest/com.sksamuel.kotest.engine.plan.ScriptDescriptorTest"
      }
   }
}
