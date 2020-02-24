import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    
}

android {
    compileSdkVersion(28)
    
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    kotlinOptions {
        val opts = this as KotlinJvmOptions
        opts.jvmTarget = "1.8"
    }
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    
    // Android
    implementation("androidx.core:core-ktx:1.2.0-rc01")
    
    // Kotest
    implementation(project(":kotest-assertions"))
}
