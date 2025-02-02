package io.kotest.framework.gradle.config

//import org.gradle.api.Named
//import org.gradle.api.file.ConfigurableFileCollection
//import org.gradle.api.model.ObjectFactory
//import org.gradle.api.provider.Property
//import org.gradle.api.tasks.Classpath
//import org.gradle.api.tasks.Input
//import org.gradle.api.tasks.Internal
//import org.gradle.kotlin.dsl.newInstance
//import javax.inject.Inject
//
///**
// * Specification of how the Kotest  should launch tests.
// */
//abstract class TestExecutionSpec @Inject internal constructor(
//   private val name: String
//) : Named {
//
////   @get:Input
////   abstract val kotlinTargetName: Property<String>
//
//   @get:Input
//   abstract val kotlinTarget: Property<KotlinTarget>
//
//   @get:Input
//   abstract val enabled: Property<Boolean>
//
//   @get:Classpath
//   abstract val classpath: ConfigurableFileCollection
//
//   @Internal
//   override fun getName(): String = name
//
//   companion object {
//      internal fun ObjectFactory.newTestExecutionSpec(): TestExecutionSpec {
//         return newInstance<TestExecutionSpec>().apply {
//            enabled.convention(true)
//         }
//      }
//   }
//}
