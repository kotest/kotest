import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
   id("com.android.application")
   kotlin("android")
   kotlin("android.extensions")
}

android {
   compileSdkVersion(30)

   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }

   kotlinOptions {
      jvmTarget = JavaVersion.VERSION_1_8.toString()
   }

   lintOptions {
      tasks.lint.get().enabled = false
   }

   defaultConfig {
      applicationId = "io.kotlintest.androidtests"
      minSdkVersion(21)
      targetSdkVersion(30)

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   testOptions {
      unitTests.isIncludeAndroidResources = true
      unitTests.isReturnDefaultValues = true
   }

   packagingOptions {
      exclude("META-INF/LICENSE.md")
      exclude("META-INF/LICENSE-notice.md")
   }

   useLibrary("android.test.runner")
   useLibrary("android.test.base")
   useLibrary("android.test.mock")

}

dependencies {
   // Kotlin
   implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
   implementation(kotlin("reflect", KotlinCompilerVersion.VERSION))
   implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

   // Android
   implementation("androidx.core:core-ktx:1.3.2")

   // Test
   testImplementation(project(Projects.Engine))
   testImplementation(project(Projects.AssertionsShared))
   testImplementation(project(Projects.JunitRunner))
   testImplementation(Libs.Robolectric.robolectric)
   testImplementation(project(Projects.extension("robolectric")))

   testImplementation("androidx.test:core:1.3.0")
   testImplementation("androidx.test:runner:1.3.0")
   testImplementation("androidx.test:rules:1.3.0")
   testImplementation("androidx.test.ext:junit:1.1.2")
   testImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

// Workaround for Robolectric https://github.com/robolectric/robolectric/issues/5092
configurations.all {
   resolutionStrategy {
      force("androidx.test:monitor:1.3.0")
   }
}

tasks {
   withType<Test> {
      useJUnitPlatform()
   }
}
