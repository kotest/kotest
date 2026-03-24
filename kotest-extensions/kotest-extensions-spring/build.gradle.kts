import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import utils.jdkRelease

plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlin.jpa)
   alias(libs.plugins.kotlin.plugin.spring)
}

// note: Spring 4 has a minimum JDK version of 17

tasks.withType<KotlinJvmCompile>().configureEach {
   compilerOptions {
      jdkRelease(providers.provider { JavaLanguageVersion.of(17) })
      jvmTarget.set(JvmTarget.JVM_17)
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(17)
}

kotlin {
   jvm()
   jvmToolchain { languageVersion = JavaLanguageVersion.of(17) }
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlin.reflect)
            implementation(libs.spring.context)
            implementation(libs.spring.test)
            implementation(libs.byte.buddy)
         }
      }
      jvmTest {
         dependencies {
            implementation(libs.flyway.database.postgresql)
            implementation(libs.spring.boot.starter)
            implementation(libs.spring.boot.starter.web)
            implementation(libs.spring.boot.starter.test)
            implementation(libs.spring.boot.starter.data.jpa)
            implementation(libs.spring.boot.starter.data.jpa.test)
            implementation(libs.spring.boot.testcontainers)
            implementation(libs.spring.boot.starter.flyway)
            implementation(libs.testcontainers.postgresql)
            implementation(libs.springmockk)
            implementation(libs.postgresql)
         }
      }
   }
}
