package io.kotest.plugin.pitest

import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.core.fromSpecClass
import io.kotest.runner.jvm.TestEngine
import io.kotest.runner.jvm.TestEngineListener
import org.pitest.testapi.Description
import org.pitest.testapi.ResultCollector
import org.pitest.testapi.TestUnit
import kotlin.reflect.KClass

class KotestUnit(val klass: KClass<out Spec>) : TestUnit {

  override fun getDescription(): Description = Description(io.kotest.Description.fromSpecClass(klass).fullName(), klass.java)

  override fun execute(rc: ResultCollector) {

    val listener = object : TestEngineListener {

      private val started = mutableSetOf<io.kotest.Description>()
      private val completed = mutableSetOf<io.kotest.Description>()

      override fun enterTestCase(testCase: TestCase) {
        if (started.add(testCase.description))
          rc.notifyStart(Description(testCase.description.fullName(), klass.java))
      }

      override fun exitTestCase(testCase: TestCase, result: TestResult) {
        val desc = Description(testCase.description.fullName(), klass.java)
        if (completed.add(testCase.description)) {
          when (result.error) {
            null -> rc.notifyEnd(desc)
            else -> rc.notifyEnd(desc, result.error)
          }
        }
      }
    }

    val engine = TestEngine(listOf(klass), emptyList(), 1, emptySet(), emptySet(), listener)
    engine.execute()
  }
}
