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
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
      js {
         val main by compilations.getting {
            kotlinOptions {
               moduleKind = "commonjs"
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

      val commonMain by getting {
         dependencies {
            api(project(":kotest-assertions"))
            api(kotlin("stdlib-common"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3")
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(kotlin("stdlib-js"))
            api(kotlin("test-js"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.3")
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
            implementation("org.slf4j:slf4j-api:1.7.28")
            implementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
            implementation("io.arrow-kt:arrow-core:0.10.3")

         }
      }
   }
}

apply(from = "../publish.gradle")
