import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate


plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinNative.get()) {
      linuxX64()
      linuxArm64()

      mingwX64()

      macosX64()
      macosArm64()

      tvosX64()
      tvosArm64()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosSimulatorArm64()

      // FIXME: The "desktop" intermediate source set can be integrated into "native". In this case
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
