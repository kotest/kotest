package io.kotest.plugin.intellij.amper

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile

/**
 * Detection helpers for Amper-managed modules.
 *
 * Amper is JetBrains' alternative build tool to Gradle / Maven, configured via a
 * `module.yaml` per module and (optionally) a `project.yaml` at the project root.
 * See https://github.com/JetBrains/amper.
 *
 * The Kotest plugin needs a way to detect Amper modules so it can produce a run
 * configuration that delegates to the `amper` wrapper instead of trying to spawn
 * the Kotest engine launcher directly — the latter fails with `ClassNotFoundException`
 * because Amper's classpath model doesn't line up with IntelliJ's "application + custom
 * main class" launcher path. See https://github.com/kotest/kotest/issues/5893.
 */
internal object AmperUtils {

   private const val MODULE_FILE = "module.yaml"

   /**
    * Returns true if [module] is an Amper-managed module — that is, any of its content roots
    * (or the project root above them) contains a `module.yaml` file.
    */
   fun isAmperModule(module: Module?): Boolean = amperModuleRoot(module) != null

   /**
    * Returns the directory containing the `module.yaml` for [module], or null if [module] is
    * not Amper-managed.
    *
    * Walks each content root up to the project root looking for a `module.yaml`. Walking up
    * is necessary because Amper's Maven-like layout puts the module file at the module root,
    * but IntelliJ may register `src/` and `test/` as separate content roots whose direct parent
    * is what holds `module.yaml`.
    */
   fun amperModuleRoot(module: Module?): VirtualFile? {
      if (module == null) return null
      val rootManager = ModuleRootManager.getInstance(module)
      for (contentRoot in rootManager.contentRoots) {
         val found = findModuleYamlFrom(contentRoot)
         if (found != null) return found
      }
      return null
   }

   private fun findModuleYamlFrom(start: VirtualFile): VirtualFile? {
      var current: VirtualFile? = if (start.isDirectory) start else start.parent
      while (current != null) {
         if (current.findChild(MODULE_FILE) != null) return current
         current = current.parent
      }
      return null
   }
}
