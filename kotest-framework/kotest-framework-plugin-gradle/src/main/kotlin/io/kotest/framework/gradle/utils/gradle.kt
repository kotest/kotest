package io.kotest.framework.gradle.utils

import org.gradle.api.DomainObjectSet
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


private val ArtifactTypeAttribute: Attribute<String> =
   Attribute.of("artifactType", String::class.java)


internal fun AttributeContainer.artifactType(value: String) {
   attribute(ArtifactTypeAttribute, value)
}


//region NDOC.all {} kinda sucks sometimes when it makes each item the receiver.
// The function is similarly named to `let { it }`.
internal fun <T : Any> DomainObjectSet<T>.letAll(
   configure: (element: T) -> Unit
): Unit =
   all(configure)
