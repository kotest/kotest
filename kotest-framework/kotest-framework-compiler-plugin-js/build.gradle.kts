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
      val commonMain by getting {
         dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
            compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
         }
      }
      val jvmMain by getting {
         dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
            compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
         }
      }
   }
}



apply(from = "../../publish-mpp.gradle.kts")
