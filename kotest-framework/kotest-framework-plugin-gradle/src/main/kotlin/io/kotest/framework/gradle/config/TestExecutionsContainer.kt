package io.kotest.framework.gradle.config

//import io.kotest.framework.gradle.config.TestExecutionSpec.Companion.newTestExecutionSpec
//import org.gradle.api.Action
//import org.gradle.api.NamedDomainObjectContainer
//import org.gradle.api.NamedDomainObjectFactory
//import org.gradle.api.NamedDomainObjectProvider
//import org.gradle.api.model.ObjectFactory
//import org.gradle.api.plugins.ExtensionAware
//import org.gradle.api.plugins.ExtensionContainer
//import org.gradle.api.specs.Spec
//import org.gradle.kotlin.dsl.domainObjectContainer
//import org.gradle.kotlin.dsl.newInstance
//import javax.inject.Inject
//
///**
// * A container for all configurable [TestExecutionSpec]s.
// */
//// Dev note: This is a wrapper for NamedDomainObjectContainer that significantly
//// reduces the API surface, keeping our code cleaner and preventing Gradle mistakes (like eager configuration).
//abstract class TestExecutionsContainer @Inject internal constructor(
//   private val objects: ObjectFactory
//) {
//   private val extensions: ExtensionContainer
//      get() = (this as ExtensionAware).extensions
//
//   private val testExecutionSpecFactory: NamedDomainObjectFactory<TestExecutionSpec> =
//      NamedDomainObjectFactory { name ->
//         val element = objects.newTestExecutionSpec()
//         extensions.add(name, element)
//         element
//      }
//
//   private val content: NamedDomainObjectContainer<TestExecutionSpec> =
//      objects.domainObjectContainer(TestExecutionSpec::class, testExecutionSpecFactory)
//
//   /**
//    * Register a new [TestExecutionSpec].
//    */
//   fun register(
//      name: String,
//      configure: Action<TestExecutionSpec>,
//   ): NamedDomainObjectProvider<TestExecutionSpec> =
//      content.register(name, configure)
//
//   /**
//    * Configure a single [TestExecutionSpec] with [name], if it exists.
//    *
//    * Does nothing if no element is matches [name].
//    */
//   fun configure(
//      name: String,
//      configure: Action<TestExecutionSpec>,
//   ) {
//      content.matching { it.name == name }.configureEach(configure)
//   }
//
//   /**
//    * Lazily configure all elements, including those added later.
//    */
//   fun configureEach(configure: Action<TestExecutionSpec>) {
//      content.configureEach(configure)
//   }
//
//   /**
//    * Return a new [TestExecutionsContainer] containing only elements that match [spec].
//    */
//   fun matching(spec: Spec<TestExecutionSpec>): TestExecutionsContainer {
//      val matches = content.matching(spec)
//      return objects.newTestExecutionsContainer().apply {
//         this.content.addAll(matches)
//      }
//   }
//
//   internal fun all(configure: Action<TestExecutionSpec>) {
//      content.all(configure)
//   }
//
//   companion object {
//      /**
//       * Create a new instance of [TestExecutionsContainer].
//       */
//      internal fun ObjectFactory.newTestExecutionsContainer(): TestExecutionsContainer =
//         newInstance<TestExecutionsContainer>()
//   }
//}
