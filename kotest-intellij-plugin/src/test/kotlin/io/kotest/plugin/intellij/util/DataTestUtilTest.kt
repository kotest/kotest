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

      val testFiles = listOf(
         "/data-test-tags/DataTestTagsFunSpec.kt",
         "/data-test-tags/DataTestTagsFreeSpec.kt"
      )

      testFiles.forEach { testFile ->
         myFixture.configureByFiles(
            testFile,
            "/io/kotest/core/spec/style/specs.kt"
         )

         val allDataTestCalls = findAllCallExpressionsByNames(DataTestUtil.styleToDataTestMethodNames.values.flatten().toSet())

         allDataTestCalls.size shouldBe 16

         // Data test inside "child context" at line 13 (withData - firstChildOfChildContext)
         val firstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 13 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "(kotest.data.13) | !kotest.data"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside firstChildOfChildContext at line 14 (withTests - firstChildOfFirstChildOfChildContext)
         // Exclude sibling at line 17
         val firstChildOfFirstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 14 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfFirstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "(kotest.data.13 & !kotest.data.17) | !kotest.data"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside firstChildOfChildContext at line 17 (withData - secondChildOfFirstChildOfChildContext)
         // Exclude sibling at line 14
         val secondChildOfFirstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 17 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, secondChildOfFirstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "(kotest.data.13 & !kotest.data.14) | !kotest.data"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside secondChildOfFirstChildOfChildContext at line 18 (withTests - firstChildOfsecondChildOfFirstChildOfChildContext)
         // Same as parent (no siblings)
         val firstChildOfsecondChildOfFirstChildOfChildContext =
            allDataTestCalls.find { getLineNumber(it) == 18 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfsecondChildOfFirstChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "(kotest.data.13 & !kotest.data.14) | !kotest.data"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside "child context" at line 23 (withTests - secondChildOfChildContext)
         val secondChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 23 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, secondChildOfChildContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "(kotest.data.23) | !kotest.data"
            ancestorTestPath shouldBe "parent context -- child context"
         }

         // Data test inside "parent context" at line 27 (withContexts - firstChildOfParentContext)
         val firstChildOfParentContext = allDataTestCalls.find { getLineNumber(it) == 27 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfParentContext).apply {
            this.shouldNotBeNull()
            tag shouldBe "(kotest.data.27) | !kotest.data"
            ancestorTestPath shouldBe "parent context"
         }

         // Root withData at line 33
         val rootWithData = allDataTestCalls.find { getLineNumber(it) == 33 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(isDataTest = true, rootWithData)?.tag shouldBe "kotest.data.33"

         // firstChild (withTests) at line 34 - exclude siblings at lines 37 and 55
         val firstChild = allDataTestCalls.find { getLineNumber(it) == 34 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.37 & !kotest.data.55"

         // secondChild (withData) at line 37 - exclude siblings at lines 34 and 55
         val secondChild = allDataTestCalls.find { getLineNumber(it) == 37 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            secondChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55"

         // firstChildOfSecondChild (withContexts) at line 38 - exclude siblings at lines 46 and 49, plus parent's siblings
         val firstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 38 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstChildOfSecondChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55 & !kotest.data.46 & !kotest.data.49"

         // firstChildOfFirstChildOfSecondChild (withTests) at line 39 - exclude sibling at line 42
         val firstChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 39 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstChildOfFirstChildOfSecondChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55 & !kotest.data.46 & !kotest.data.49 & !kotest.data.42"

         // secondChildOfFirstChildOfSecondChild (withTests) at line 42 - exclude sibling at line 39
         val secondChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 42 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            secondChildOfFirstChildOfSecondChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55 & !kotest.data.46 & !kotest.data.49 & !kotest.data.39"

         // secondChildOfSecondChild (withTests) at line 46 - exclude siblings at lines 38 and 49
         val secondChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 46 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            secondChildOfSecondChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55 & !kotest.data.38 & !kotest.data.49"

         // thirdChildOfSecondChild (withData) at line 49 - exclude siblings at lines 38 and 46
         val thirdChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 49 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            thirdChildOfSecondChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55 & !kotest.data.38 & !kotest.data.46"

         // firstAndOnlyChildOfThirdChildOfSecondChild (withTests) at line 50 - same as parent (no siblings)
         val firstAndOnlyChild = allDataTestCalls.find { getLineNumber(it) == 50 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            firstAndOnlyChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.55 & !kotest.data.38 & !kotest.data.46"

         // thirdChild (withTests) at line 55 - exclude siblings at lines 34 and 37
         val thirdChild = allDataTestCalls.find { getLineNumber(it) == 55 }.shouldNotBeNull()
         DataTestUtil.dataTestInfoMaybe(
            isDataTest = true,
            thirdChild
         )?.tag shouldBe "kotest.data.33 & !kotest.data.34 & !kotest.data.37"
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
