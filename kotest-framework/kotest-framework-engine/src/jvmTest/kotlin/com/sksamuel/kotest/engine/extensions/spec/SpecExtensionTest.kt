package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class SpecExtensionTest : WordSpec() {

   init {

      var before = 0
      var after = 0

      register(object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            before++
            process()
            after++
         }
      })

      afterProject {
         after shouldBe 2
         error("qwewqe")
      }

      "SpecExtensions" should {
         "be activated by registration" {
            before shouldBe 1
            after shouldBe 0
         }
      }

   }
}

var beforeInstancePerLeaf = 0
var afterInstancePerLeaf = 0

class SpecExtensionIsolationModePerTestTest : WordSpec() {
   init {

      isolationMode = IsolationMode.InstancePerLeaf

      afterProject {
         beforeInstancePerLeaf shouldBe 1
         afterInstancePerLeaf shouldBe 2
         error("qwewqe")
      }

      register(object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            beforeInstancePerLeaf++
            process()
            afterInstancePerLeaf++
         }
      })

      "SpecExtensions" should {
         "fire first for this instance" {
            //before shouldBe 1
            //after shouldBe 0
         }
         "fire again for this instance" {
            // before shouldBe 2
            //after shouldBe 1
         }
      }
   }
}
