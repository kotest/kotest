package io.kotest.extensions.pitest

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.runBlocking
import org.pitest.testapi.Description
import org.pitest.testapi.ResultCollector
import org.pitest.testapi.TestUnit
import kotlin.reflect.KClass

class KotestUnit(val kclass: KClass<out Spec>) : TestUnit {

   override fun getDescription(): Description = Description(kclass.java.name, kclass.java)

   override fun execute(rc: ResultCollector) = runBlocking {
      val description = getDescription()
      val listener = object : AbstractTestEngineListener() {

         private val started = mutableSetOf<Descriptor.TestDescriptor>()
         private val completed = mutableSetOf<Descriptor.TestDescriptor>()

         override suspend fun testStarted(testCase: TestCase) {
            if (started.add(testCase.descriptor)) {
               rc.notifyStart(Description(testCase.descriptor.path().value, kclass.java))
            }
         }

         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            val desc = Description(testCase.descriptor.path().value, kclass.java)
            if (completed.add(testCase.descriptor)) {
               when (result.errorOrNull) {
                  null -> rc.notifyEnd(desc)
                  else -> rc.notifyEnd(desc, result.errorOrNull)
               }
            }
         }

         // a spec that fails outside its tests (for example in beforeSpec, or when it cannot be
         // instantiated) is only reported here, so pitest must hear about it under the spec itself
         override suspend fun specFinished(ref: SpecRef, result: TestResult) {
            val error = result.errorOrNull ?: return
            rc.notifyStart(description)
            rc.notifyEnd(description, error)
         }
      }

      val result = TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(listOf(SpecRef.Reference(kclass, kclass.java.name)))
         .execute()

      if (result.errors.isNotEmpty())
         error("Test suite failed with errors")
   }
}
