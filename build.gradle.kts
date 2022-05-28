allprojects {
   apply(plugin = "com.adarshr.test-logger")

   repositories {
      mavenCentral()
      mavenLocal()
      maven("https://oss.sonatype.org/content/repositories/snapshots/")
      google()
   }

   group = "io.kotest"
   version = Ci.publishVersion

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions {
         freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         jvmTarget = "1.8"
         apiVersion = "1.6"
         languageVersion = "1.6"
      }
   }

   tasks.withType<Test> {
   }
}

// TODO: Remove me after https://youtrack.jetbrains.com/issue/KT-49109 is fixed
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
   rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
}
