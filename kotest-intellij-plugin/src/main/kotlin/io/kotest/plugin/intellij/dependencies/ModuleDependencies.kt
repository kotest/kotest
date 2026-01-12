package io.kotest.plugin.intellij.dependencies

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager

object ModuleDependencies {
   fun hasKotest(module: Module?): Boolean {
      if (module == null) return false
      var hasKotest = false
      ModuleRootManager.getInstance(module).orderEntries().forEachLibrary {
         if (it.name?.contains("kotest-framework-engine") == true)
            hasKotest = true
         true
      }
      return hasKotest
   }
}
