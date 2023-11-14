//plugins {
//   id("kotest-js-conventions")
//   // must be a published version and not one in the current build
//   id("io.kotest.multiplatform").version("5.5.4")
//}
//
//kotlin {
//   sourceSets {
//      val commonTest by getting {
//         dependencies {
//            implementation(projects.kotestAssertions.kotestAssertionsCore)
//            implementation(projects.kotestFramework.kotestFrameworkEngine)
//            implementation(projects.kotestFramework.kotestFrameworkDatatest)
//         }
//      }
//   }
//}

// must be a published version and not one in the current build
//configure<io.kotest.framework.multiplatform.gradle.KotestPluginExtension> {
//   compilerPluginVersion.set("5.8.0")
//}
