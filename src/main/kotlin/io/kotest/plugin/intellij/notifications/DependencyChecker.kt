package io.kotest.plugin.intellij.notifications

import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.impl.NotificationFullContent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

object DependencyChecker {

   private val Group = NotificationGroup("Kotest", NotificationDisplayType.BALLOON, true)

   private val ConsoleDep = Dependency(
      "io.kotest",
      "kotest-runner-console-jvm",
      "4.1",
      "io.kotest.runner.console.TeamCityConsoleWriter"
   )

   private val ReflectDep = Dependency("org.jetbrains.kotlin",
      "kotlin-reflect",
      "1.3.72",
      "kotlin.reflect.jvm.internal.ReflectProperties")

   private val RequiredDeps = listOf(ConsoleDep, ReflectDep)

//   private fun OrderEnumerator.libraries(): List<Dependency> {
//      val libraries = mutableListOf<Dependency>()
//      forEachLibrary { library ->
//         val dep = library.name?.removePrefix("Gradle: ")?.removePrefix("Maven: ")
//         if (dep != null) {
//            val components = dep.split(':')
//            if (components.size == 3) {
//               val (group, artifact, version) = components
//               libraries.add(Dependency(group, artifact, version))
//            }
//         }
//         true
//      }
//      return libraries.toList()
//   }

   //private fun libraries(module: Module): List<Dependency> = OrderEnumerator.orderEntries(module).libraries()

   private fun hasDependency(dep: Dependency, module: Module): Boolean {
      val facade = JavaPsiFacade.getInstance(module.project)
      return facade.findClass(dep.fqn, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true)) != null
   }
//      if (locationModule != null && !Comparing.equal(project.getBasePath(), locationVirtualFile != null ? locationVirtualFile.getPath() : null)) {
//         for (String fqn : fqns) {
//         if () != null) return true;
//      }
//      }
//      return LocationUtil.isJarAttached(context.getLocation(), psiPackage, dep.fqn)
//   }
//      libraries(module).any { it.group == dep.group && it.artifact == dep.artifact }

   fun checkMissingDependencies(module: Module): Boolean {
      RequiredDeps.forEach {
         if (!hasDependency(it, module)) {
            showDependencyNotification(it, module.project)
            return false
         }
      }
      return true
   }

   private fun showDependencyNotification(dep: Dependency, project: Project) {

      val notification = object : Notification(
         Group.displayId,
         "Kotest",
         "Add <b>${dep.asString()}</b> to your build to execute tests using the kotest plugin. Required version is ${dep.version} or higher.",
         NotificationType.ERROR
      ), NotificationFullContent {}


      val openMavenAction = object : AnAction("Open maven central") {
         override fun actionPerformed(e: AnActionEvent) {
            BrowserUtil.browse("https://search.maven.org/search?q=g:${dep.group}%20AND%20a:${dep.artifact}")
         }
      }

      notification.addAction(openMavenAction)

      Notifications.Bus.notify(notification, project)
   }
}

data class Dependency(val group: String, val artifact: String, val version: String, val fqn: String) {
   fun asString(): String = "${group}:${artifact}"
}
