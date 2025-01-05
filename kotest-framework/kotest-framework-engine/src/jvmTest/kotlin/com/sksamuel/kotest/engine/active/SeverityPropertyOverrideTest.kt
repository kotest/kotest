package com.sksamuel.kotest.engine.active

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

@Isolate
class SeverityPropertyOverrideTest : WordSpec({

   "Default severity without override, prefix normal" should {
      var run = false
      System.setProperty(KotestEngineProperties.testSeverity, "NORMAL")
      "allow this test to run" {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeTrue()
   }

   "Default severity without override, prefix higher" should {
      var run = false
      System.setProperty(KotestEngineProperties.testSeverity, "CRITICAL")
      "allow this test to run" {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeFalse()
   }

   "Default severity without override, prefix lower" should {
      var run = false
      System.setProperty(KotestEngineProperties.testSeverity, "MINOR")
      "allow this test to run" {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeTrue()
   }

   "Critical severity, prefix NORMAL" should {
      var run = false
       System.setProperty(KotestEngineProperties.testSeverity, "NORMAL")
      "allow this test to run".config(severity = TestCaseSeverityLevel.CRITICAL) {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeTrue()
   }

   "Critical severity, prefix BLOCKER" should {
      var run = false
      System.setProperty(KotestEngineProperties.testSeverity, "BLOCKER")
      "not allow this test to run".config(severity = TestCaseSeverityLevel.CRITICAL) {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeFalse()
   }

   "MINOR severity, prefix TRIVIAL" should {
      var run = false
      System.setProperty(KotestEngineProperties.testSeverity, "TRIVIAL")
      "allow this test to run".config(severity = TestCaseSeverityLevel.MINOR) {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeTrue()
   }


   "MINOR severity, prefix NORMAL" should {
      var run = false
      System.setProperty(KotestEngineProperties.testSeverity, "NORMAL")
      "allow this test to run".config(severity = TestCaseSeverityLevel.MINOR) {
         run = true
      }
      System.getProperties().remove(KotestEngineProperties.testSeverity)
      run.shouldBeFalse()
   }

   "MINOR severity, prefix default" should {
      var run = false
      "allow this test to run".config(severity = TestCaseSeverityLevel.MINOR) {
         run = true
      }
      run.shouldBeTrue()
   }
})
