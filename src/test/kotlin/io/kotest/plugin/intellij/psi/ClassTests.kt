package io.kotest.plugin.intellij.psi

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.IdeaTestUtil
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class ClassTests : BasePlatformTestCase() {

   private val descriptor = object : ProjectDescriptor(LanguageLevel.HIGHEST) {
      override fun getSdk(): Sdk {
         return IdeaTestUtil.getMockJdk17()
      }

//      override fun configureModule(module: Module, model: ModifiableRootModel, contentEntry: ContentEntry) {
//         PsiTestUtil.addLibrary(
//            module,
//            "kotest-framework-api-jvm-4.3.0.jar",
//            "/home/sam/.gradle/caches/modules-2/files-2.1/io.kotest/kotest-framework-api-jvm/4.3.0/810fac77ecce307b58cfccc741965a4e87b09622",
//            "kotest-framework-api-jvm-4.3.0.jar"
//         )
//      }
   }

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   override fun getProjectDescriptor(): LightProjectDescriptor = descriptor

   fun testEnclosingClass() {

      val psiFile = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val element = psiFile[0].elementAtLine(21)
      element.shouldNotBeNull()
      val ktclass = element.enclosingKtClass()
      ktclass.shouldNotBeNull()
      ktclass.fqName?.asString() shouldBe "io.kotest.samples.gradle.FunSpecExampleTest"
   }

   fun testSuperClasses() {
      val psiFile = myFixture.configureByFiles(
         "/io/kotest/plugin/intellij/abstractspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )[0]
      val supers = psiFile.elementAtLine(11)?.enclosingKtClass()!!.getAllSuperClasses().map { it.asString() }
      // the order varies depending on the intellij version, so using set to compare
      supers.toSet() shouldBe setOf("io.kotest.plugin.intellij.MyParentSpec", "io.kotest.core.spec.style.FunSpec")
   }

   fun testClasses() {
      val psiFile = myFixture.configureByFile("/classes/childktclasses.kt")
      psiFile.classes().map { it.name } shouldBe listOf(
         "Child1",
         "Child2",
         "Child3",
         "Child4",
         "Child5",
         "Child6",
         "Child7",
         "Child8",
         "Child9",
         "Child10",
         "Child11",
         "Child12"
      )
   }
}
