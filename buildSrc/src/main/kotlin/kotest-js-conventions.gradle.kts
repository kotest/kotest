import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY)) {
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
               withWasmJs() // FIXME with Kotlin 2.0.0: KT-63417 – to be split into `withWasmJs` and `withWasmWasi`
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
