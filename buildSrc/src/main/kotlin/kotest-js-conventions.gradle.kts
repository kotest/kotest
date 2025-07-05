import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
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

      @OptIn(ExperimentalWasmDsl::class)
      wasmJs {
         browser()
         nodejs()
      }

      /* FIXME: enable wasmWasi when there is support in kotlinx-coroutines-core (1.8.0-RC does only wasmJs)
      wasmWasi {
         nodejs()
      }
      */

      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            group("jsHosted") {
               withJs()
               withWasmJs()
               withWasmWasi()
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
