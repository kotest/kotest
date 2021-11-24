plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
   mavenLocal()
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            compileOnly(Libs.Kotlin.compiler)
         }
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
