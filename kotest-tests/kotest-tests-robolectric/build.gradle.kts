import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
   id("com.android.application")
   kotlin("android")
   kotlin("kapt")
}

android {
   compileSdkVersion(30)

   testOptions {
      unitTests {
         isReturnDefaultValues = true
         isIncludeAndroidResources = true
      }
   }

   buildFeatures {
      viewBinding = true
   }

   defaultConfig {
      applicationId = "com.sksamuel.robolectric"
      minSdkVersion(21)
      targetSdkVersion(30)
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   compileOptions {
      // Flag to enable support for the new language APIs
      isCoreLibraryDesugaringEnabled = true
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8

   }
   kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = freeCompilerArgs + listOf(
         "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
      )
   }

   useLibrary("android.test.runner")
   useLibrary("android.test.base")
   useLibrary("android.test.mock")

   packagingOptions {
      exclude("META-INF/LICENSE.md")
      exclude("META-INF/LICENSE-notice.md")
   }
}

dependencies {
   implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
   implementation(kotlin("script-runtime", KotlinCompilerVersion.VERSION))
   implementation(AndroidLibs.KotlinX.coroutinesAndroid)

   implementation(AndroidLibs.AndroidX.coreKtx)
   implementation(AndroidLibs.AndroidX.appCompat)
   implementation(AndroidLibs.AndroidX.constraintLayout)
   implementation(AndroidLibs.AndroidX.ArchCore.common)
   implementation(AndroidLibs.AndroidX.Fragment.fragmentKtx)
   implementation(AndroidLibs.AndroidX.Navigation.runtimeKtx)
   implementation(AndroidLibs.AndroidX.Navigation.fragmentKtx)
   implementation(AndroidLibs.AndroidX.Navigation.uiKtx)
   implementation(AndroidLibs.AndroidX.Lifecycle.runtimeKtx)
   implementation(AndroidLibs.AndroidX.Lifecycle.liveDataKtx)
   implementation(AndroidLibs.AndroidX.Lifecycle.viewModelKtx)

   coreLibraryDesugaring(AndroidLibs.desugarJdk)

   testDependencies()
}

tasks {
   withType<Test> {
      useJUnitPlatform()
   }
}

fun DependencyHandlerScope.testDependencies() {
   testImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
   testImplementation(kotlin("reflect", KotlinCompilerVersion.VERSION))
   testImplementation(Libs.Coroutines.test)
   testImplementation(AndroidLibs.liveDataTestingKtx)
   testImplementation(AndroidLibs.AndroidX.ArchCore.testing)
   testImplementation(AndroidLibs.AndroidX.Testing.extJUnit)
   testImplementation(AndroidLibs.AndroidX.Espresso.core)

   testImplementation(project(Projects.JunitRunner))
   testImplementation(project(Projects.AssertionsCore))
   testImplementation(project(Projects.Property))

   testImplementation(AndroidLibs.AndroidX.Testing.runner)
   testImplementation(AndroidLibs.AndroidX.Testing.rules)
   testImplementation(project(Projects.extension("robolectric")))
   testImplementation(AndroidLibs.robolectric)

   androidTestImplementation(AndroidLibs.AndroidX.Espresso.core)
   debugImplementation(AndroidLibs.AndroidX.Fragment.testing)
}

// Workaround for Robolectric https://github.com/robolectric/robolectric/issues/5092
configurations.all {
   resolutionStrategy {
      force("androidx.test:monitor:1.3.0")
   }
}
