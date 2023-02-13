@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   id("com.android.library") version "7.3.0"
   id("org.jetbrains.kotlin.android") version "1.6.21"
}

android {
   namespace = "com.example.myapplication"
   compileSdk = 32

   defaultConfig {
      minSdk = 24
      targetSdk = 32

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      }
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }
   kotlinOptions {
      jvmTarget = "1.8"
   }
}

val kotestPublishVersion: String by project

dependencies {
   implementation("androidx.core:core-ktx:1.7.0")
   implementation("androidx.appcompat:appcompat:1.4.1")
   implementation("com.google.android.material:material:1.5.0")

   testImplementation("junit:junit:4.13.2")
   testImplementation("io.kotest:kotest-assertions-core:$kotestPublishVersion")
   testImplementation("io.kotest:kotest-assertions-json:$kotestPublishVersion")

   androidTestImplementation("androidx.test.ext:junit:1.1.3")
   androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

   androidTestDebugImplementation("io.kotest:kotest-assertions-core:$kotestPublishVersion")
   androidTestDebugImplementation("io.kotest:kotest-assertions-json:$kotestPublishVersion")

   androidTestImplementation("io.kotest:kotest-assertions-core:$kotestPublishVersion")
   androidTestImplementation("io.kotest:kotest-assertions-json:$kotestPublishVersion")
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions.allWarningsAsErrors = true
}

java {
   toolchain {
      languageVersion.set(JavaLanguageVersion.of(11))
   }
}
