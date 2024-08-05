import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import utils.SystemPropertiesArgumentProvider

plugins {
   id("kotest-base")
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

testlogger {
   showPassed = false
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()

   val kotestSystemProps = providers.systemPropertiesPrefixedBy("kotest")
   jvmArgumentProviders += SystemPropertiesArgumentProvider(kotestSystemProps)
   filter {
      isFailOnNoMatchingTests = false
   }
}

kotlin {
   @OptIn(ExperimentalKotlinGradlePluginApi::class)
   compilerOptions {
      freeCompilerArgs.add("-Xexpect-actual-classes")
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
