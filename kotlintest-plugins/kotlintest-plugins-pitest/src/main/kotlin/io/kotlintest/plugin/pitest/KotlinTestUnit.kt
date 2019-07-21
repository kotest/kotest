package io.kotlintest.plugin.pitest

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.runner.jvm.TestEngine
import io.kotlintest.runner.jvm.TestEngineListener
import org.pitest.testapi.Description
import org.pitest.testapi.ResultCollector
import org.pitest.testapi.TestUnit
import kotlin.reflect.KClass

class KotlinTestUnit(val klass: KClass<out Spec>) : TestUnit {

  override fun getDescription(): Description = Description(io.kotlintest.Description.spec(klass).fullName(), klass.java)

  override fun execute(rc: ResultCollector) {

    val listener = object : TestEngineListener {

      private val started = mutableSetOf<io.kotlintest.Description>()
      private val completed = mutableSetOf<io.kotlintest.Description>()

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
