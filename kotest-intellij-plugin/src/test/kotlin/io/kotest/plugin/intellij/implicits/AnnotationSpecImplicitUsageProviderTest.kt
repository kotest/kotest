package io.kotest.plugin.intellij.implicits

import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.nio.file.Paths

class AnnotationSpecImplicitUsageProviderTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getProjectDescriptor(): LightProjectDescriptor = JAVA_11

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   override fun runInDispatchThread(): Boolean = false

   fun testLifecycleFunctionsAreMarkedAsUsed() {
      val psiFiles = myFixture.configureByFiles(
         "/annotationspeclifecycle.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val provider = AnnotationSpecImplicitUsageProvider()

      ApplicationManager.getApplication().runReadAction {
         val functions = PsiTreeUtil.findChildrenOfType(psiFiles[0], KtNamedFunction::class.java)

         val beforeEach = functions.find { it.name == "beforeEachTest" }!!
         val afterEach = functions.find { it.name == "afterEachTest" }!!
         val beforeAll = functions.find { it.name == "beforeAllTests" }!!
         val afterAll = functions.find { it.name == "afterAllTests" }!!
         val test = functions.find { it.name == "myTest" }!!
         val ignored = functions.find { it.name == "ignoredTest" }!!

         provider.isImplicitUsage(beforeEach) shouldBe true
         provider.isImplicitUsage(afterEach) shouldBe true
         provider.isImplicitUsage(beforeAll) shouldBe true
         provider.isImplicitUsage(afterAll) shouldBe true
         provider.isImplicitUsage(test) shouldBe true
         provider.isImplicitUsage(ignored) shouldBe true
      }
   }
}
