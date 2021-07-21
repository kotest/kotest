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
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
            compileOnly("org.jetbrains.kotlin:kotlin-compiler:1.5.21")
         }
      }
   }
}



apply(from = "../../publish-mpp.gradle.kts")
