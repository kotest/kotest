import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import utils.jdkRelease

plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

// note: JUnit6 has a minimum JDK version of 17

kotlin {
   jvm()
   jvmToolchain { languageVersion = JavaLanguageVersion.of(17) }
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
         }
      }

      jvmMain {
         dependencies {
            api(projects.kotestCommon)
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(projects.kotestRunner.kotestRunnerJunitPlatform)
            api(libs.kotlinx.coroutines.core)
            api(libs.junit.platform6.engine)
            api(libs.junit.platform6.api)
            api(libs.junit.platform6.launcher)
            api(libs.junit.jupiter6.api)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform6.testkit)
            implementation(libs.mockk)
         }
      }
   }
}

tasks.withType<KotlinJvmCompile>().configureEach {
   compilerOptions {
      jdkRelease(providers.provider { JavaLanguageVersion.of(17) })
      jvmTarget.set(JvmTarget.JVM_17)
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(17)
}
