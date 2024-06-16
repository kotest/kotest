import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   `java-library`
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

testlogger {
   showPassed = false
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
}

extensions.configure<KotlinMultiplatformExtension> {
   @OptIn(ExperimentalKotlinGradlePluginApi::class)
   compilerOptions {
      allWarningsAsErrors = true
      optIn.addAll(
         "io.kotest.common.KotestInternal",
         "io.kotest.common.ExperimentalKotest",
         "kotlin.time.ExperimentalTime",
         "kotlin.experimental.ExperimentalTypeInference",
         "kotlin.contracts.ExperimentalContracts",
      )
      freeCompilerArgs.addAll(
         "-Xexpect-actual-classes",
      )
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions {
      compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(8)
}
