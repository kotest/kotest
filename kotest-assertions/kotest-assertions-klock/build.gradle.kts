plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   jcenter()
}

kotlin {
   sourceSets {

      targets.all {
         compilations.all {
            kotlinOptions {
               freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
            }
         }
      }

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib-common"))
            implementation(Libs.Klock.klock)
            implementation(project(":kotest-assertions"))
         }
      }
   }
}

apply(from = "../../publish.gradle")
