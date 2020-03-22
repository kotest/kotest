package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object ShouldSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotest.core.spec.style.ShouldSpec")

  override fun specStyleName(): String = "ShouldSpec"

  override fun generateTest(specName: String, name: String): String {
    return "should(\"$name\") { }"
  }

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateContainerTests(): List<String> {
    val test = tryContainer()
    return when {
      test != null && parent != null -> parent.locateContainerTests() + test
      test != null -> listOf(test)
      parent != null -> parent.locateContainerTests()
      else -> emptyList()
    }
  }

  private fun PsiElement.tryShouldWithConfig(): String? {
    val should = extractStringArgForFunctionBeforeDotExpr(listOf("should"), listOf("config"))
    return if (should == null) null else "should $should"
  }

  private fun PsiElement.tryShould(): String? {
    val should = matchFunction2WithStringAndLambda(listOf("should"))
    return if (should == null) null else "should $should"
  }

  private fun PsiElement.tryContainer(): String? = matchStringInvoke()

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec()) return null
    val leaf = element.tryShould() ?: element.tryShouldWithConfig()
    val container = element.tryContainer()
    return when {
      leaf != null -> {
        val parents = element.locateContainerTests()
        if (parents.isEmpty()) leaf else {
          val tests = (parents + leaf).distinct()
          tests.dropLast(1).joinToString(" -- ") + " " + tests.last()
        }
      }
      container != null ->
        (element.locateContainerTests() + container).distinct().joinToString(" -- ")
      else -> null
    }
  }
}