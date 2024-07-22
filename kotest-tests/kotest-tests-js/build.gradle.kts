plugins {
   id("kotest-js-conventions")
   // must be a published version and not one in the current build
   id("io.kotest.multiplatform").version("5.8.1")
}

kotlin {
   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}

// must be a published version and not one in the current build
configure<io.kotest.framework.multiplatform.gradle.KotestPluginExtension> {
   compilerPluginVersion.set("5.3.0")
}
