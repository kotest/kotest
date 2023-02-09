@file:Suppress("UnstableApiUsage")

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

dependencies {
   implementation("androidx.core:core-ktx:1.7.0")
   implementation("androidx.appcompat:appcompat:1.4.1")
   implementation("com.google.android.material:material:1.5.0")
   testImplementation("junit:junit:4.13.2")
   androidTestImplementation("androidx.test.ext:junit:1.1.3")
   androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

   testImplementation(fileTree("../build/android-test-dependencies/") { include("*.jar") })
}
