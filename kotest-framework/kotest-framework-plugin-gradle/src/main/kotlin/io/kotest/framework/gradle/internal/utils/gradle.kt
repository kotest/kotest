package io.kotest.framework.gradle.internal.utils

import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectContainer


/**
 * Create a new [NamedDomainObjectContainer], using
 * [org.gradle.kotlin.dsl.domainObjectContainer]
 * (but [T] is `reified`).
 *
 * @param[factory] an optional factory for creating elements
 * @see org.gradle.kotlin.dsl.domainObjectContainer
 */
internal inline fun <reified T : Any> ObjectFactory.domainObjectContainer(
   factory: NamedDomainObjectFactory<T>? = null
): NamedDomainObjectContainer<T> =
   if (factory == null) {
      domainObjectContainer(T::class)
   } else {
      domainObjectContainer(T::class, factory)
   }


/**
 * `artifactType` is a special Gradle attribute used to determine the files inside a
 * [org.gradle.api.artifacts.Configuration].
 */
private val ArtifactTypeAttribute: Attribute<String> =
   Attribute.of("artifactType", String::class.java)

/**
 * Set the value of the [ArtifactTypeAttribute] inside this [AttributeContainer].
 */
internal fun AttributeContainer.artifactType(value: String) {
   attribute(ArtifactTypeAttribute, value)
}


/**
 * [NDOC.all {}][NamedDomainObjectContainer.all] kinda sometimes sucks because
 * it makes each item the receiver, which can be confusing when nested.
 *
 * This function helps by using an actual parameter.
 *
 * The name is inspired by [let] - `let { it -> ... }`.
 */
internal fun <T : Any> DomainObjectCollection<T>.letAll(
   configure: (element: T) -> Unit
): Unit =
   all(configure)
