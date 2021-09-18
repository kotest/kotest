plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
   mavenLocal()
}

kotlin {
   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
               freeCompilerArgs
            }
         }
      }
   }
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            compileOnly(Libs.Kotlin.compilerEmbeddable)
         }
      }
   }
}



apply(from = "../../publish-mpp.gradle.kts")
