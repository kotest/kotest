import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY)&&kotestSettings.enableKotlinNative.get()) {
      watchosDeviceArm64()

      // TODO: The "desktop" intermediate source set can be integrated into "native". In this case
      //     the following block can be replaced with `applyDefaultHierarchyTemplate()`.
      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            group("desktop") {
               withNative()
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
