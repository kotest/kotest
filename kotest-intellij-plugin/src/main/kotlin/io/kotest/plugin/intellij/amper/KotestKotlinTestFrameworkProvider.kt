package io.kotest.plugin.intellij.amper

import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import io.kotest.plugin.intellij.psi.isRunnableSpec
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.idea.extensions.KotlinTestFrameworkProvider
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Teaches the Amper IntelliJ plugin (and any other consumer of this Kotlin extension point)
 * to recognise Kotest specs as test classes.
 *
 * `AmperKotlinTestConfigurationProducer` iterates `org.jetbrains.kotlin.idea.testFrameworkProvider`
 * extensions and asks each one to identify the test class for a given PSI element. The Amper
 * producer then builds a class-level `amper test --include-classes <fqn>` run configuration —
 * which is exactly what we want for Kotest specs.
 *
 * To stay well-behaved we scope this provider to Amper-managed modules only. Outside of Amper
 * we have our own producers (Gradle, IDEA-flavour); we don't want this provider to influence
 * unrelated Kotlin test discovery in non-Amper projects.
 *
 * See https://github.com/kotest/kotest/issues/5893.
 */
class KotestKotlinTestFrameworkProvider : KotlinTestFrameworkProvider {

   override val canRunJvmTests: Boolean = true

   override fun isProducedByJava(configuration: ConfigurationFromContext): Boolean = false

   override fun isProducedByKotlin(configuration: ConfigurationFromContext): Boolean = false

   override fun isTestFrameworkAvailable(element: PsiElement): Boolean = inAmperModule(element)

   override fun isTestJavaClass(testClass: PsiClass): Boolean {
      if (!inAmperModule(testClass)) return false
      val origin = (testClass as? KtLightClass)?.kotlinOrigin ?: return false
      return origin.isRunnableSpec()
   }

   /**
    * Kotest tests are DSL invocations rather than methods, and Amper's CLI only exposes class-level
    * filtering (`--include-classes`), so we never claim a test method.
    */
   override fun isTestJavaMethod(testMethod: PsiMethod): Boolean = false

   private fun inAmperModule(element: PsiElement): Boolean {
      val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return false
      return AmperUtils.isAmperModule(module)
   }
}
