plugins {
   id("kotest-js-conventions")
   // must be a published version and not one in the current build
   id("io.kotest.multiplatform").version("5.0.3")
}

kotlin {
   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Framework.datatest))
         }
      }
   }
}

// must be a published version and not one in the current build
configure<io.kotest.framework.multiplatform.gradle.KotestPluginExtension> {
   compilerPluginVersion.set("5.0.3")
}
