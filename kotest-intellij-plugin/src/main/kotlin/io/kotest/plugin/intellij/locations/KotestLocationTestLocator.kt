package io.kotest.plugin.intellij.locations

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * A [SMTestLocator] that handles a parsed [KotestLocation] format.
 */
internal class KotestLocationTestLocator(private val location: KotestLocation) : SMTestLocator, DumbAware {

   override fun getLocation(
      protocol: String,
      path: String,
      project: Project,
      scope: GlobalSearchScope
   ): List<Location<PsiElement>> {
      if (DumbService.isDumb(project)) return emptyList()

      val fqn = DescriptorPaths.fqn(location.path)
      val psiClass: PsiClass = ClassUtil.findPsiClass(
         /* manager = */ PsiManager.getInstance(project),
         /* name = */ fqn,
         /* parent = */ null,
         /* jvmCompatible = */ true,
         /* scope = */ scope
      ) ?: return emptyList()

      val contexts = DescriptorPaths.contexts(location.path)
      if (contexts.isEmpty()) return listOf(createClassNavigable(psiClass))

      val ktClass = psiClass.navigationElement as? KtClassOrObject ?: return emptyList()
      val style = ktClass.specStyle() ?: return emptyList()

      val tests = style.tests(ktClass, false)
      val test = findTest(tests, contexts) ?: return emptyList()
      return listOf(createTestNavigable(test))
   }

   private fun findTest(tests: List<TestElement>, contexts: List<String>): TestElement? {
      val test = tests.find { it.test.name.name == contexts.first() } ?: return null
      if (contexts.size == 1) return test
      return findTest(test.nestedTests, contexts.drop(1))
   }

   private fun createClassNavigable(psiClass: PsiClass): Location<PsiElement> {
      return PsiLocation(psiClass.project, psiClass)
   }

   private fun createTestNavigable(test: TestElement): Location<PsiElement> {
      return PsiLocation(test.psi.project, test.psi)
   }
}

// todo create a shared module for this and the similar logic from the engine
internal object DescriptorPaths {

   const val SPEC_DELIMITER = "/"
   const val TEST_DELIMITER = " -- "

   fun fqn(string: String): String {
      // we know the spec name has to be included in a descriptor path
      return string.substringBefore(SPEC_DELIMITER)
   }

   fun contexts(string: String): List<String> {
      // we know the spec name has to be included in a descriptor path
      val testsString = string.trim().substringAfter(SPEC_DELIMITER, "")
      if (testsString.isBlank()) return emptyList()
      return testsString.split(TEST_DELIMITER)
   }
}
