package io.kotest.plugin.intellij.run

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

private const val mainClass421 = "io.kotest.engine.launcher.MainKt"
private const val mainClass420 = "io.kotest.framework.launcher.LauncherKt"
private const val mainClass41x = "io.kotest.launcher.LauncherKt"
private const val test421 = "io.kotest.engine.launcher.MainKt"
private const val test420 = "io.kotest.engine.KotestEngine"

private val kotest41xRequiredJars = listOf(
   "io.kotest.launcher.TeamCityMessages",
   "com.github.ajalt.clikt.core.CliktCommand",
   "com.github.ajalt.mordant.TermColors"
)

private val kotest420RequiredJars = listOf(
   // the launcher and console writers
   "io.kotest.framework.console.TeamCityMessages",
   // for discovery of specs
   "io.kotest.framework.discovery.Discovery",
   // clickt to parse command line
   "com.github.ajalt.clikt.core.CliktCommand",
   // mordaunt for pretty printing
   "com.github.ajalt.mordant.TermColors"
)

private val kotest41xparams = listOf(
   "--writer", "teamcity"
)

private val kotest420params = listOf(
   "--writer", "teamcity"
)

private val kotest421params = listOf(
   "--reporter", "teamcity"
)

private fun is421(project: Project): Boolean {
   val scope = GlobalSearchScope.allScope(project)
   return JavaPsiFacade.getInstance(project).findClass(test421, scope) != null
}

private fun is420(project: Project): Boolean {
   val scope = GlobalSearchScope.allScope(project)
   return JavaPsiFacade.getInstance(project).findClass(test420, scope) != null
}

/**
 *
 * In 4.2.1, the launcher(main method) is part of engine to accommodate the gradle plugin
 * In 4.2.0, the launcher was in its own module - io.kotest.framework.launcher
 * In 4.1.3, we included the launcher as a separate dependency explicitly
 * In <4.1.3 users were required to add console dependency to the build
 */
@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
fun determineKotestLauncher(project: Project): LauncherConfig {
   return when {
      is421(project) -> LauncherConfig(mainClass421, emptyList(), kotest421params)
      is420(project) -> LauncherConfig(mainClass420, kotest420RequiredJars, kotest420params)
      else -> LauncherConfig(mainClass41x, kotest41xRequiredJars, kotest41xparams)
   }
}

data class LauncherConfig(
   val mainClass: String,
   val requiredJars: List<String>,
   val params: List<String>
)



