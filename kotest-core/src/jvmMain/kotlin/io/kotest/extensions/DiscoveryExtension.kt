package io.kotest.extensions

import io.kotest.SpecClass
import kotlin.reflect.KClass

/**
 * Allows interception of the discovery phase of Kotest.
 *
 * The discovery phase is the part of the test cycle that finds
 * possible [SpecClass] classes on the classpath and then instantiates them
 * ready to be executed.
 *
 * Note: If multiple [DiscoveryExtension]s are registered, the order
 * in which they execute is not specified.
 */
interface DiscoveryExtension : ProjectLevelExtension {

  /**
   * Invoked as soon as the scan phase is complete. At that point,
   * the [SpecClass] classes have been detected, but not yet instantiated
   * or executed.
   *
   * Overriding this function gives implementations the possibility
   * of filtering the specs seen by the test engine.
   *
   * For instance, a possible extension may filter tests by package
   * name, class name, classes that only implement a certain
   * interface, etc.
   *
   * @param classes the [KClass] for each discovered [SpecClass]
   *
   * @return  the list of filtered classes to use.
   */
  fun afterScan(classes: List<KClass<out SpecClass>>): List<KClass<out SpecClass>>
}
