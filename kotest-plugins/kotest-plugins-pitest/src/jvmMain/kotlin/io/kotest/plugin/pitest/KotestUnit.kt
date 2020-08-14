package io.kotest.plugin.pitest

import io.kotest.core.spec.DisplayName
import io.kotest.core.test.DescriptionType
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.AbstractSpec
import io.kotest.engine.launcher.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.test.TestName
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlinx.coroutines.runBlocking
import org.pitest.testapi.Description
import org.pitest.testapi.ResultCollector
import org.pitest.testapi.TestUnit
import kotlin.reflect.KClass

fun KClass<*>.toDescription(): io.kotest.core.test.Description {
   val name = annotation<DisplayName>()?.name ?: bestName()
   return io.kotest.core.test.Description(null, TestName(name), DescriptionType.Spec, this)
}

class KotestUnit(val klass: KClass<out AbstractSpec>) : TestUnit {

   override fun getDescription(): Description = Description(klass.toDescription().displayPath(false), klass.java)

   override fun execute(rc: ResultCollector) = runBlocking {

      val listener = object : TestEngineListener {

         private val started = mutableSetOf<io.kotest.core.test.Description>()
         private val completed = mutableSetOf<io.kotest.core.test.Description>()

         override fun testStarted(testCase: TestCase) {
            if (started.add(testCase.description))
               rc.notifyStart(Description(testCase.description.displayPath(false), klass.java))
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            val desc = Description(testCase.description.displayPath(false), klass.java)
            if (completed.add(testCase.description)) {
               when (result.error) {
                  null -> rc.notifyEnd(desc)
                  else -> rc.notifyEnd(desc, result.error)
               }
            }
         }
      }

      KotestEngineLauncher(listener).forSpec(klass).launch()
   }
}
