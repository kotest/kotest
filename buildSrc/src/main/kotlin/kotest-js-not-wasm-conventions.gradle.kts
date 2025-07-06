import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinJs.get()) {
      js {
         browser()
         nodejs()
      }

      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            group("jsHosted") {
               withJs()
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
