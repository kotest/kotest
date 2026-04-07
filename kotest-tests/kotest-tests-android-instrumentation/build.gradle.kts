import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
   id("kotlin-conventions")
   alias(libs.plugins.android.library)
   alias(libs.plugins.jetbrains.kotlin.android)
   alias(libs.plugins.jetbrains.kotlin.compose)
}

kotlin {
   jvm()
   jvmToolchain { languageVersion = JavaLanguageVersion.of(21) }
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestRunner.kotestRunnerJunit4)
            implementation(projects.kotestAssertions.kotestAssertionsCore)

            // sets the versions for all the compose libraries
//            val composeBom = platform(libs.androidx.compose.bom)
//            implementation(composeBom)
//            testImplementation(composeBom)
//            androidTestImplementation(composeBom)

            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.material)

            // dependencies for device-side tests
//            androidTestImplementation(libs.androidx.junit)
//            androidTestImplementation(libs.androidx.test.runner)
//            androidTestImplementation(libs.androidx.ui.test.junit4)
         }
      }
   }
}

tasks.withType<KotlinJvmCompile>().configureEach {
   compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(21)
}
