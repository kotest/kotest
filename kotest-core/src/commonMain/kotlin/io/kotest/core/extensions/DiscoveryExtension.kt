package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Allows interception of the discovery phase of Kotest.
 *
 * The discovery phase is the part of the test cycle that finds
 * [Spec] classes on the classpath.
 *
 * This extension is only usable on the JVM target.
 *
 * Note: If multiple [DiscoveryExtension]s are registered, the order
 * in which they execute is not specified.
 */
interface DiscoveryExtension : Extension {

   /**
    * Invoked as soon as the scan phase has completed. At this point,
    * the [Spec] classes have been detected, but not yet
    * instantiated or executed.
    *
    * Overriding this function gives implementations the possibility
    * of filtering the specs seen by the test engine.
    *
    * For instance, a possible extension may filter tests by package
    * name, class name, classes that only implement a certain
    * interface, etc.
    *
    * @param classes the [KClass] for each discovered [Spec]
    *
    * @return  the list of filtered classes to use.
    */
   fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}
