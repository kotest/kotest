import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
   id("kotlin-conventions")
}

kotlin {
   jvm()
   jvmToolchain { languageVersion = JavaLanguageVersion.of(21) }
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
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
