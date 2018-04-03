package io.kotlintest.extensions

import io.kotlintest.Description
import io.kotlintest.Spec
import kotlin.reflect.KClass

/**
 * Allows interception of the discovery phase of KotlinTest.
 *
 * The discovery phase is the part of the test cycle that finds
 * possible [Spec] classes on the classpath and then instantiates them
 * ready to be executed.
 *
 * Note: If multiple [DiscoveryExtension]s are registered, the order
 * in which they execute is not specified.
 */
interface DiscoveryExtension : Extension {

  /**
   * Invoked as soon as the scan phase is complete.
   * At that point, the [Spec] classes have been detected, but
   * not yet instantiated or executed.
   *
   * Overriding this function gives implementations the possibility
   * of filtering the specs seen by the Test Runner.
   *
   * For instance a possible extension may filter any tests for
   * Windows machines if it is executing in a Linux environment.
   *
   * @param descriptions the [Description] for each discovered [Spec]
   *
   * @return  the list of filtered specs to use.
   */
  fun afterScan(descriptions: List<Description>): List<Description> = descriptions

  /**
   * An extension function invoked to create an instance of a [Spec].
   *
   * An implementation can choose to create a new instance, or it can
   * choose to return null if it wishes to pass control to the next
   * extension (or if no more extensions, then back to the Test Runner).
   *
   * By overriding this function, extensions are able to customize
   * the way classes are created, to support things like constructors
   * with parameters, or classes that require special initization logic.
   */
  fun <T : Spec> instantiate(clazz: KClass<T>): Spec? = null
}