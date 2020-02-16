package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

interface ConstructorExtension : Extension {

   /**
    * This function is invoked to create an instance of a [Spec].
    *
    * By default, test classes are assumed to have zero-arg primary constructors.
    * If you wish to use non-zero arg primary constructors, this function can
    * be implemented with logic on how to instantiate a test class.
    *
    * An implementation can choose to create a new instance, or it can
    * choose to return null if it wishes to pass control to the next
    * extension (or if no more extensions, then back to the Engine).
    *
    * By overriding this function, extensions are able to customize
    * the way classes are created, to support things like constructors
    * with parameters, or classes that require special initization logic.
    *
    * One very common usecase is to instantiate classes that are being autowired
    * by a dependency injection framework.
    */
   fun <T : Spec> instantiate(clazz: KClass<T>): Spec?
}
