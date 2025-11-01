import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinTest
import utils.SystemPropertiesArgumentProvider

plugins {
   id("kotest-base")
   kotlin("multiplatform")
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()

   val kotestSystemProps: Provider<Map<String, String>> = providers.systemPropertiesPrefixedBy("kotest")
   jvmArgumentProviders += SystemPropertiesArgumentProvider(kotestSystemProps)
   filter {
      isFailOnNoMatchingTests = false
   }
}

kotlin {
   @OptIn(ExperimentalKotlinGradlePluginApi::class)
   compilerOptions {
      freeCompilerArgs.add("-Xexpect-actual-classes")
      freeCompilerArgs.add("-Xwhen-guards")
      apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
      languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
      allWarningsAsErrors = false
   }
   sourceSets.configureEach {
      languageSettings {
         optIn("io.kotest.common.ExperimentalKotest")
         optIn("io.kotest.common.KotestInternal")
         optIn("kotlin.contracts.ExperimentalContracts")
         optIn("kotlin.experimental.ExperimentalTypeInference")
         optIn("kotlin.time.ExperimentalTime")
      }
   }
}

tasks.withType<KotlinTest>().configureEach {
   failOnNoDiscoveredTests = false
}
