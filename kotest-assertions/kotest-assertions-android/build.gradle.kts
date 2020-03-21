buildscript {
   repositories {
      google()
   }
   dependencies {
      classpath("com.android.tools.build:gradle:3.5.3")
   }
}

plugins {
   id("org.jetbrains.kotlin.multiplatform")
   id("com.android.library")
}

repositories {
   mavenCentral()
   google()
   jcenter()
}

kotlin {
   android {
      publishLibraryVariants("release")
   }
}


android {
   compileSdkVersion(28)
   defaultConfig {
     minSdkVersion(16)
     targetSdkVersion(28)
   }
   buildTypes {
      getByName("release") {
         isMinifyEnabled = false
      }
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }
   sourceSets {
      getByName("main") {
         manifest.srcFile("src/androidMain/AndroidManifest.xml")
         java.srcDirs("src/androidMain/kotlin")
         dependencies {
            implementation(kotlin("stdlib-jdk8", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
            implementation(project(":kotest-assertions"))
            implementation("androidx.core:core-ktx:1.2.0")
         }
      }
   }
}
