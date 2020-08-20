package io.kotest.plugin.intellij.run

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

// if we have the new launcher on the classpath we're on kotest 4.2+
// if we have the old launcher on the classpath we're on kotest 4.1
private const val mainClass42 = "io.kotest.framework.launcher.LauncherKt"
private const val mainClass41 = "io.kotest.launcher.LauncherKt"
private const val engine42 = "io.kotest.engine.KotestEngine"

private val kotest41RequiredJars = listOf(
   "io.kotest.launcher.TeamCityMessages",
   "com.github.ajalt.clikt.core.CliktCommand",
   "com.github.ajalt.mordant.TermColors"
)

private val kotest42RequiredJars = listOf(
   // the launcher and console writers
   "io.kotest.framework.console.TeamCityMessages",
   // for discovery of specs
   "io.kotest.framework.discovery.Discovery",
   // clickt to parse command line
   "com.github.ajalt.clikt.core.CliktCommand",
   // mordaunt for pretty printing
   "com.github.ajalt.mordant.TermColors"
)

/**
 * In 4.2, the framework classes have moved packages. So we must determine which
 * launcher we want to use. We can do this by checking for a class that only
 * exists in the 4.2 engine.
 */
fun determineKotestLauncher(project: Project): LauncherConfig {
   val scope = GlobalSearchScope.allScope(project)
   return when (JavaPsiFacade.getInstance(project).findClass(engine42, scope)) {
      null -> LauncherConfig(mainClass41, kotest41RequiredJars)
      else -> LauncherConfig(mainClass42, kotest42RequiredJars)
   }
}

data class LauncherConfig(
   val mainClass: String,
   val requiredJars: List<String>
)



