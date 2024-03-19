import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
   kotlin("multiplatform") version "1.9.23"
   id("io.kotest.multiplatform") version "+"
}

repositories {
   mavenCentral {
      mavenContent { excludeGroup("io.kotest") }
   }
}

kotlin {
   js {
      browser()
      nodejs()
   }

   @OptIn(ExperimentalWasmDsl::class)
   wasmJs {
      browser()
      nodejs()
   }

//   @OptIn(ExperimentalWasmDsl::class)
//   wasmWasi {
//      nodejs()
//   }

   sourceSets {
      commonTest {
         dependencies {
            implementation("io.kotest:kotest-assertions-core:+")
            implementation("io.kotest:kotest-framework-engine:+")
            implementation("io.kotest:kotest-framework-datatest:+")
         }
      }
   }
}

configure<io.kotest.framework.multiplatform.gradle.KotestPluginExtension> {
   kotestCompilerPluginVersion.set("+")
}
