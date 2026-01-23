package io.kotest.plugin.intellij.util

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import org.jetbrains.kotlin.psi.KtCallExpression
import java.nio.file.Paths

class DataTestUtilTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testDataTestTagMaybeReturnsTagExpressionForNestedDataTests() {
      testMode = true

      // StringSpec is not tested as it does not allow nesting at any level, as it does not expose containers
      // and the simple use case of data tests at root level is already covered in the other tests
      val testFiles = listOf(
         "/data-test-tags/DataTestTagsFunSpec.kt",
         "/data-test-tags/DataTestTagsFreeSpec.kt",
         "/data-test-tags/DataTestTagsBehaviorSpec.kt",
         "/data-test-tags/DataTestTagsDescribeSpec.kt",
         "/data-test-tags/DataTestTagsExpectSpec.kt",
         "/data-test-tags/DataTestTagsFeatureSpec.kt",
         "/data-test-tags/DataTestTagsShouldSpec.kt",
         "/data-test-tags/DataTestTagsWordSpec.kt",
      )

      testFiles.forEach { testFile ->
         myFixture.configureByFiles(
            testFile,
            "/io/kotest/core/spec/style/specs.kt"
         )

         val allDataTestCalls = findAllCallExpressionsByNames(DataTestUtil.styleToDataTestMethodNames.values.flatten().toSet())

         allDataTestCalls.size shouldBe 16

         // Data test inside "child context" at line 16 (withData - firstChildOfChildContext)
         val firstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 16 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "((kotest.data.16) | !kotest.data) | kotest.data.nonJvm"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside firstChildOfChildContext at line 17 (withTests - firstChildOfFirstChildOfChildContext)
         // Exclude sibling at line 20
         val firstChildOfFirstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 17 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfFirstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "((kotest.data.16 & !kotest.data.20) | !kotest.data) | kotest.data.nonJvm"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside firstChildOfChildContext at line 20 (withData - secondChildOfFirstChildOfChildContext)
         // Exclude sibling at line 17
         val secondChildOfFirstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 20 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, secondChildOfFirstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "((kotest.data.16 & !kotest.data.17) | !kotest.data) | kotest.data.nonJvm"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside secondChildOfFirstChildOfChildContext at line 21 (withTests - firstChildOfsecondChildOfFirstChildOfChildContext)
         // Same as parent (no siblings)
         val firstChildOfsecondChildOfFirstChildOfChildContext =
            allDataTestCalls.find { getLineNumber(it) == 21 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfsecondChildOfFirstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "((kotest.data.16 & !kotest.data.17) | !kotest.data) | kotest.data.nonJvm"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside "child context" at line 26 (withTests - secondChildOfChildContext)
         val secondChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 26 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, secondChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "((kotest.data.26) | !kotest.data) | kotest.data.nonJvm"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside "parent context" at line 30 (withContexts - firstChildOfParentContext)
         val firstChildOfParentContext = allDataTestCalls.find { getLineNumber(it) == 30 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfParentContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "((kotest.data.30) | !kotest.data) | kotest.data.nonJvm"
            ancestorTestPath shouldBe "parent context"
         }

         // Root withData at line 36
         val rootWithData = allDataTestCalls.find { getLineNumber(it) == 36 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, rootWithData)?.tag shouldBe "(kotest.data.36) | kotest.data.nonJvm"

         // firstChild (withTests) at line 37 - exclude siblings at lines 40 and 58
         val firstChild = allDataTestCalls.find { getLineNumber(it) == 37 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.40 & !kotest.data.58) | kotest.data.nonJvm"

         // secondChild (withData) at line 40 - exclude siblings at lines 37 and 58
         val secondChild = allDataTestCalls.find { getLineNumber(it) == 40 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            secondChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58) | kotest.data.nonJvm"

         // firstChildOfSecondChild (withContexts) at line 41 - exclude siblings at lines 49 and 52, plus parent's siblings
         val firstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 41 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstChildOfSecondChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52) | kotest.data.nonJvm"

         // firstChildOfFirstChildOfSecondChild (withTests) at line 42 - exclude sibling at line 45
         val firstChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 42 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstChildOfFirstChildOfSecondChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52 & !kotest.data.45) | kotest.data.nonJvm"

         // secondChildOfFirstChildOfSecondChild (withTests) at line 45 - exclude sibling at line 42
         val secondChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 45 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            secondChildOfFirstChildOfSecondChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52 & !kotest.data.42) | kotest.data.nonJvm"

         // secondChildOfSecondChild (withTests) at line 49 - exclude siblings at lines 41 and 52
         val secondChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 49 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            secondChildOfSecondChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.52) | kotest.data.nonJvm"

         // thirdChildOfSecondChild (withData) at line 52 - exclude siblings at lines 41 and 49
         val thirdChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 52 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            thirdChildOfSecondChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.49) | kotest.data.nonJvm"

         // firstAndOnlyChildOfThirdChildOfSecondChild (withTests) at line 53 - same as parent (no siblings)
         val firstAndOnlyChild = allDataTestCalls.find { getLineNumber(it) == 53 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstAndOnlyChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.49) | kotest.data.nonJvm"

         // thirdChild (withTests) at line 58 - exclude siblings at lines 37 and 40
         val thirdChild = allDataTestCalls.find { getLineNumber(it) == 58 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            thirdChild
         )?.tag shouldBe "(kotest.data.36 & !kotest.data.37 & !kotest.data.40) | kotest.data.nonJvm"
      }
   }

   private fun findAllCallExpressionsByNames(names: Set<String>): List<KtCallExpression> {
      val psiFile = myFixture.file
      return PsiTreeUtil.findChildrenOfType(psiFile, KtCallExpression::class.java)
         .filter { it.calleeExpression?.text in names }
   }

   private fun getLineNumber(psi: com.intellij.psi.PsiElement): Int {
      val file = psi.containingFile ?: return -1
      val document = file.viewProvider.document ?: return -1
      return document.getLineNumber(psi.textOffset) + 1
   }
}
