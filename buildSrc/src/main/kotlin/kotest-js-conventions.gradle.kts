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

      wasmJs {
         browser()
         nodejs()
      }

      /* FIXME: enable wasmWasi when there is support in kotlinx-coroutines-core (1.8.0-RC does only wasmJs)
      wasmWasi {
         nodejs()
      }
      */

      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            group("jsHosted") {
               withJs()
               withWasm() // FIXME: KT-63417 – to be split into `withWasmJs` and `withWasmWasi`
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}

// Use a Node.js version current enough to support Kotlin/Wasm

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
   rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
      // Initialize once in a multi-project build.
      // Otherwise, Gradle would complain "Configuration already finalized for previous property values".
      if (!System.getProperty("nodeJsCanaryConfigured").toBoolean()) {
         nodeVersion = "22.0.0-v8-canary20231213fc7703246e"
         println("Using Node.js $nodeVersion to support Kotlin/Wasm")
         nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
         System.setProperty("nodeJsCanaryConfigured", "true")
      }
   }
}

rootProject.tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
   args.add("--ignore-engines") // Prevent Yarn from complaining about newer Node.js versions.
}
