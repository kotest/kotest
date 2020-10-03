package com.sksamuel.kotest.engine.active

import io.kotest.core.config.configuration
import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class SeverityPropertyOverrideTest : WordSpec({

         "Default severity without override, prefix normal" should {
            var run = false
            System.setProperty(KotestEngineSystemProperties.severityPrefix, "NORMAL")
            "allow this test to run" {
               run = true
            }
            System.getProperties().remove(KotestEngineSystemProperties.severityPrefix)
            run.shouldBeTrue()
         }

         "Default severity without override, prefix higher" should {
            var run = false
            System.setProperty(KotestEngineSystemProperties.severityPrefix, "CRITICAL")
            "!allow this test to run" {
               run = true
            }
            System.getProperties().remove(KotestEngineSystemProperties.severityPrefix)
            run.shouldBeFalse()
         }

         "Default severity without override, prefix lower" should {
            var run = false
            System.setProperty(KotestEngineSystemProperties.severityPrefix, "MINOR")
            "allow this test to run" {
               run = true
            }
            System.getProperties().remove(KotestEngineSystemProperties.severityPrefix)
            run.shouldBeTrue()
         }

         "Critical severity, prefix normal" should {
            var run = false
            System.setProperty(KotestEngineSystemProperties.severityPrefix, "NORMAL")
            "allow this test to run".config(severity = TestCaseSeverityLevel.CRITICAL) {
               run = true
            }
            System.getProperties().remove(KotestEngineSystemProperties.severityPrefix)
            run.shouldBeTrue()
         }

         "Critical severity, prefix blocker" should {
            var run = false
            System.setProperty(KotestEngineSystemProperties.severityPrefix, "BLOCKER")
            "!allow this test to run".config(severity = TestCaseSeverityLevel.CRITICAL) {
               run = true
            }
            System.getProperties().remove(KotestEngineSystemProperties.severityPrefix)
            run.shouldBeFalse()
         }

         "MINOR severity, prefix trivial" should {
            var run = false
            System.setProperty(KotestEngineSystemProperties.severityPrefix, "TRIVIAL")
            "allow this test to run".config(severity = TestCaseSeverityLevel.MINOR) {
               run = true
            }
            System.getProperties().remove(KotestEngineSystemProperties.severityPrefix)
            run.shouldBeTrue()
         }

         "MINOR severity, prefix default" should {
            var run = false
            "allow this test to run".config(severity = TestCaseSeverityLevel.MINOR) {
               run = true
            }
            run.shouldBeTrue()
         }
      }
)
