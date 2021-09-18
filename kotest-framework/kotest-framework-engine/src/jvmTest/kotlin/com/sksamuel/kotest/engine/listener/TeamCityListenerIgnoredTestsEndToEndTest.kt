package com.sksamuel.kotest.engine.listener

import io.kotest.core.descriptors.append
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class TeamCityListenerIgnoredTestsEndToEndTest : FunSpec() {

   private val kclass: KClass<out Spec> = TeamCityListenerIgnoredTestsEndToEndTest::class

   private val testCase1 = TestCase(
      descriptor = kclass.toDescriptor().append("disabled test"),
      name = TestName("disabled test"),
      spec = this,
      test = { },
      source = sourceRef(),
      type = TestType.Test,
      config = TestCaseConfig(enabled = false),
      factoryId = null,
   )

   private val testCase2 = testCase1.copy(
      descriptor = kclass.toDescriptor().append("disabled container"),
      type = TestType.Container,
   )

   init {
      test("an inactive spec should have tests marked as ignored") {
         val out = captureStandardOut {
            val listener = TeamCityTestEngineListener()
            listener.specEnter(kclass)
            listener.specInactive(
               kclass,
               mapOf(
                  testCase1 to TestResult.ignored("no love"),
                  testCase2 to TestResult.ignored("rejected")
               )
            )
            listener.specExit(kclass, null)
         }
         out.trim() shouldBe
            """
##teamcity[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest' id='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest:1' test_type='spec']

##teamcity[testIgnored name='disabled test' id='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest/disabled_test' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest:1' test_type='test' message='no love' result_status='Ignored']

##teamcity[testIgnored name='disabled container' id='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest/disabled_container' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest:1' test_type='container' message='rejected' result_status='Ignored']

##teamcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest' id='com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerIgnoredTestsEndToEndTest:1' result_status='Success' test_type='spec']

""".trim()
      }
   }
}
