package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.common.concurrentHashMap
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

@KotestInternal
class CollectingTestEngineListener : TestEngineListener {

   val specs = concurrentHashMap<KClass<*>, Throwable?>()
   val tests = concurrentHashMap<TestCase, TestResult>()
   val names = mutableListOf<String>()
   var errors = false

   override suspend fun executionStarted(node: Node) {
   }

   override suspend fun executionIgnored(node: Node, reason: String?) {
      when (node) {
         is Node.Engine -> Unit
         is Node.Spec -> Unit
         is Node.Test -> {
            tests[node.testCase] = TestResult.Ignored(reason)
            names.add(node.testCase.name.testName)
         }
      }
   }

   override suspend fun executionFinished(node: Node, result: TestResult) {
      when (node) {
         is Node.Engine -> {
            if (result.errorOrNull != null) errors = true
         }
         is Node.Spec -> {
            specs[node.kclass] = result.errorOrNull
            if (result.errorOrNull != null) errors = true
         }
         is Node.Test -> {
            tests[node.testCase] = result
            if (result.isFailure || result.isError) errors = true
            names.add(node.testCase.name.testName)
         }
      }
   }

   fun result(descriptor: Descriptor.TestDescriptor): TestResult? = tests.mapKeys { it.key.descriptor }[descriptor]
   fun result(testname: String): TestResult? = tests.mapKeys { it.key.name.testName }[testname]
}
