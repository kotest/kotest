package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.Issue
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.EngineDescriptorBuilder
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.createUniqueIdForSpec
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry

@Issue("https://github.com/kotest/kotest/issues/3770")
class DynamicSpecRegistrationTest : FunSpec() {
   init {
      test("JUnitTestEngineListener should dynamically register spec if not currently registered") {

         var registered: TestDescriptor? = null
         var started: TestDescriptor? = null

         val engineExecutionListener = object : EngineExecutionListener {
            override fun executionFinished(testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult) {}
            override fun reportingEntryPublished(testDescriptor: TestDescriptor?, entry: ReportEntry?) {}
            override fun executionSkipped(testDescriptor: TestDescriptor?, reason: String?) {}
            override fun executionStarted(testDescriptor: TestDescriptor?) {
               started = testDescriptor
            }

            override fun dynamicTestRegistered(testDescriptor: TestDescriptor?) {
               registered = testDescriptor
            }
         }

         val rootId = UniqueId.forEngine("kotest")
         val root = EngineDescriptorBuilder.builder(rootId).build()
         val listener = JUnitTestEngineListener(engineExecutionListener, root, DisplayNameFormatting(null))
         listener.specStarted(SpecRef.Reference(DynamicSpecRegistrationTest::class))

         val specId = createUniqueIdForSpec(root.uniqueId, DescriptorId(DynamicSpecRegistrationTest::class.java.name))
         registered.shouldNotBeNull().uniqueId shouldBe specId
         started.shouldNotBeNull().uniqueId shouldBe specId
      }
   }
}
