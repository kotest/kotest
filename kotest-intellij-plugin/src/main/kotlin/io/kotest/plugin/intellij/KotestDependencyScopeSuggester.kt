package io.kotest.plugin.intellij

import com.intellij.openapi.roots.DependencyScope
import com.intellij.openapi.roots.LibraryDependencyScopeSuggester
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryUtil
import com.intellij.openapi.vfs.VirtualFile

class KotestDependencyScopeSuggester : LibraryDependencyScopeSuggester() {

   private val jarMarkers = listOf("io.kotest.core.spec.style.FunSpec")

   override fun getDefaultDependencyScope(library: Library): DependencyScope? {
      val files = library.getFiles(OrderRootType.CLASSES)
      val testJars = files.count { isTestJarRoot(it) }
      val regularJars = files.size - testJars
      return if (testJars > regularJars) DependencyScope.TEST else null
   }

   private fun isTestJarRoot(file: VirtualFile): Boolean {
      for (marker in jarMarkers) {
         if (LibraryUtil.isClassAvailableInLibrary(listOf(file), marker)) {
            return true
         }
      }
      return false
   }
}
