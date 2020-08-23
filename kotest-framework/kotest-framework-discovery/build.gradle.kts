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
            implementation(kotlin("reflect"))

            // needed to compile against Spec
            // but runtime classes must be provided by modules using discovery
            compileOnly(project(Projects.Api))

            // needed to scan the classpath for classes
            implementation(Libs.Classgraph.classgraph)
         }
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.3"
}

apply(from = "../../publish-mpp.gradle.kts")
