@file:Suppress("UnstableApiUsage")

package io.kotest.plugin.intellij.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.jvm.DefaultJvmElementVisitor
import com.intellij.lang.jvm.JvmClass
import com.intellij.lang.jvm.JvmElementVisitor
import com.intellij.lang.jvm.inspection.JvmLocalInspection
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import io.kotest.plugin.intellij.psi.isSubclassOfSpec
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass

class NoZeroArgConstructorInspection : JvmLocalInspection() {

   override fun buildVisitor(project: Project,
                             sink: HighlightSink,
                             isOnTheFly: Boolean): JvmElementVisitor<Boolean> = object : DefaultJvmElementVisitor<Boolean> {
      override fun visitClass(clazz: JvmClass): Boolean {

         val element = clazz.sourceElement ?: return true

         if (element !is KtUltraLightClass) return true
         val vfile = element.containingFile?.virtualFile ?: return true

         // we only care about test files
         if (!ProjectRootManager.getInstance(project).fileIndex.isInTestSourceContent(vfile))
            return true

         // must be a spec
         if (!element.isSubclassOfSpec()) return true

         val cstrs = element.constructors
         if (cstrs.isEmpty()) return true

         val hasZeroArgConstructor = element.constructors.any { it.parameters.isEmpty() }
         if (hasZeroArgConstructor) return true

         sink.highlight(
            "Classes must have a zero arg constructor to be instantiated by Kotest",
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
         return true
      }
   }
}
