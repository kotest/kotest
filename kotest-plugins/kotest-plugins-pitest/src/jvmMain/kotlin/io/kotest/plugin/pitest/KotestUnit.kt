package io.kotest.plugin.pitest

import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.runner.jvm.KotestEngine
import io.kotest.runner.jvm.TestEngineListener
import org.pitest.testapi.Description
import org.pitest.testapi.ResultCollector
import org.pitest.testapi.TestUnit
import kotlin.reflect.KClass

class KotestUnit(val klass: KClass<out SpecConfiguration>) : TestUnit {

   override fun getDescription(): Description = Description(klass.description().fullName(), klass.java)

   override fun execute(rc: ResultCollector) {

      val listener = object : TestEngineListener {

         private val started = mutableSetOf<io.kotest.core.Description>()
         private val completed = mutableSetOf<io.kotest.core.Description>()

         override fun testStarted(testCase: TestCase) {
            if (started.add(testCase.description))
               rc.notifyStart(Description(testCase.description.fullName(), klass.java))
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            val desc = Description(testCase.description.fullName(), klass.java)
            if (completed.add(testCase.description)) {
               when (result.error) {
                  null -> rc.notifyEnd(desc)
                  else -> rc.notifyEnd(desc, result.error)
               }
            }
         }
      }

      val engine = KotestEngine(listOf(klass), emptyList(), 1, emptySet(), emptySet(), listener)
      engine.execute()
   }
}
