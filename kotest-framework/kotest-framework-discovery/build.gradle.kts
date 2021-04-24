plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            implementation(Libs.Kotlin.kotlinScriptRuntime)

            api(project(Projects.Common))
            // needed to compile against Spec
            // but runtime classes must be provided by modules using discovery
            compileOnly(project(Projects.Api))

            // needed to scan the classpath for classes
            implementation(Libs.Classgraph.classgraph)
         }
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.4"
}

apply(from = "../../publish-mpp.gradle.kts")
