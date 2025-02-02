package io.kotest.framework.gradle.config

import io.kotest.framework.gradle.config.TestCandidate.Companion.newTestCandidate
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.specs.Spec
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * A container for all configurable [TestCandidate]s.
 */
// Dev note: This is a wrapper for NamedDomainObjectContainer that significantly
// reduces the API surface, preventing user mistakes.
abstract class TestCandidates @Inject internal constructor(
   private val objects: ObjectFactory
) {
   private val extensions: ExtensionContainer
      get() = (this as ExtensionAware).extensions

   private val testCandidateFactory: NamedDomainObjectFactory<TestCandidate> =
      NamedDomainObjectFactory { name ->
         val element = objects.newTestCandidate()
         extensions.add(name, element)
         element
      }

   private val content: NamedDomainObjectContainer<TestCandidate> =
      objects.domainObjectContainer(TestCandidate::class, testCandidateFactory)

   /**
    * Register a new [TestCandidate].
    */
   fun register(
      name: String,
      configure: Action<TestCandidate>,
   ): NamedDomainObjectProvider<TestCandidate> =
      content.register(name, configure)

   /**
    * Configure a single [TestCandidate] with [name], if it exists.
    *
    * Does nothing if no element is matches [name].
    */
   fun configure(
      name: String,
      configure: Action<TestCandidate>,
   ) {
      content.matching { it.name == name }.configureEach(configure)
   }

   /**
    * Lazily configure all elements.
    */
   fun configureEach(configure: Action<TestCandidate>) {
      content.configureEach(configure)
   }

   /**
    * Return a new container containing elements that match [spec].
    */
   fun matching(spec: Spec<TestCandidate>): TestCandidates {
      val matches = content.matching(spec)
      return objects.newTestCandidates().apply {
         content.addAll(matches)
      }
   }

   internal fun all(configure: Action<TestCandidate>) {
      content.all(configure)
   }

   companion object {
      internal fun ObjectFactory.newTestCandidates(): TestCandidates =
         newInstance<TestCandidates>()
   }
}
