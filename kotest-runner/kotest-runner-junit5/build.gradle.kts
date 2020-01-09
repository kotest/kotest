plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         targets {
            jvm {
               compilations.all {
                  kotlinOptions {
                     jvmTarget = "1.8"
                  }
               }
            }
         }
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            api(project(":kotest-core"))
//            api(project(":kotest-runner:kotest-runner-console"))
            api(project(":kotest-runner:kotest-runner-jvm"))
            api("org.junit.platform:junit-platform-engine:1.5.2")
            api("org.junit.platform:junit-platform-suite-api:1.5.2")
            api("org.junit.platform:junit-platform-launcher:1.5.2")
            api("org.junit.jupiter:junit-jupiter-api:5.5.2")
            api("org.slf4j:slf4j-api:1.7.25")
         }
      }
   }
}


apply(from = "../../publish.gradle")
