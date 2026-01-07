package io.kotest.plugin.intellij

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.psi.CallbackType
import io.kotest.plugin.intellij.psi.callbacks
import io.kotest.plugin.intellij.psi.specs
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Paths

class CallbacksTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   override fun runInDispatchThread(): Boolean {
      return false
   }

   fun testCallbacks() {

      val psiFile = myFixture.configureByFiles(
         "/callbacks.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )[0]

      ApplicationManager.getApplication().runReadAction {
         val ktclass = psiFile.specs()[0]
         val callbacks = ktclass.callbacks()

         callbacks.shouldHaveSize(10)

         callbacks[0].psi.startOffset shouldBe 115
         callbacks[0].type shouldBe CallbackType.BeforeTest

         callbacks[1].psi.startOffset shouldBe 138
         callbacks[1].type shouldBe CallbackType.AfterTest

         callbacks[2].psi.startOffset shouldBe 160
         callbacks[2].type shouldBe CallbackType.BeforeSpec

         callbacks[3].psi.startOffset shouldBe 183
         callbacks[3].type shouldBe CallbackType.AfterSpec

         callbacks[4].psi.startOffset shouldBe 225
         callbacks[4].type shouldBe CallbackType.AfterEach

         callbacks[5].psi.startOffset shouldBe 243
         callbacks[5].type shouldBe CallbackType.BeforeEach

         callbacks[6].psi.startOffset shouldBe 261
         callbacks[6].type shouldBe CallbackType.AfterContainer

         callbacks[7].psi.startOffset shouldBe 288
         callbacks[7].type shouldBe CallbackType.BeforeContainer

         callbacks[8].psi.startOffset shouldBe 311
         callbacks[8].type shouldBe CallbackType.BeforeAny

         callbacks[9].psi.startOffset shouldBe 328
         callbacks[9].type shouldBe CallbackType.AfterAny
      }
   }
}
