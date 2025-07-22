plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
   //TODO, once AGP is working: id("com.android.library")
   alias(libs.plugins.kotlin.serialization)
}


kotlin {
   sourceSets {
      //TODO wire android plugin once working
      // this is required to have an android target distinct from a JVM target
      // androidTarget()
      commonMain {
         dependencies {
            implementation(libs.xmlutil)
            implementation(libs.kotlinx.io.core)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
//TODO android {} configuration block as required by the android library plugin

