package io.kotlintest.extensions

import io.kotlintest.Description
import io.kotlintest.Spec

/**
 * Allows interception of the discovery phase of KotlinTest.
 *
 * That is, after [Spec] classes have been discovered on the classpath
 * but before execution begins. This gives implementations of this
 * extension the possibility of filtering the specs seen by
 * the Test Runner.
 *
 * Note: If multiple [DiscoveryExtension]s are registered, the order
 * in which they execute is not specified.
 */
interface DiscoveryExtension {

  /**
   * Invoked as soon as the discovery phase is complete.
   * At that point, the [Spec] classes have been loaded, but
   * not yet executed.
   *
   * @param descriptions the [Description] for each discovered [Spec]
   *
   * @return  the list of filtered specs to use.
   */
  fun afterDiscovery(descriptions: List<Description>): List<Description>
}