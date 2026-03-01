import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
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
   outputs.upToDateWhen { false }
   testLogging {
      events(TestLogEvent.FAILED)
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

abstract class TimerService : BuildService<BuildServiceParameters.None>, OperationCompletionListener {
   override fun onFinish(event: FinishEvent) {
      if (event is TaskFinishEvent) {
         val duration = event.result.endTime - event.result.startTime
         if (duration > 1000) { // Only log tasks slower than 1s
            println("Task ${event.descriptor.name} took ${duration}ms")
         }
      }
   }
}

// Inject the registry as a property
val listenerRegistry = objects.newInstance(RegistryWrapper::class.java).registry

// Define a simple wrapper to facilitate injection in the script
interface RegistryWrapper {
   @get:Inject
   val registry: BuildEventsListenerRegistry
}

val serviceProvider = gradle.sharedServices.registerIfAbsent("taskTimer", TimerService::class) {}
listenerRegistry.onTaskCompletion(serviceProvider)
