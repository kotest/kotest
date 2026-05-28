package io.kotest.engine.launcher

import io.github.classgraph.ClassGraph
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Uses the [ClassGraph](https://github.com/classgraph/classgraph) library to scan the classpath
 * for all concrete, non-private subclasses of [Spec], returning them as [KClass] instances.
 *
 * This is used by the launcher when the `--specs scan` argument is provided, so that the set of
 * specs to execute can be discovered at runtime rather than being passed in explicitly.
 */
object SpecScanner {

   fun scan(): List<KClass<out Spec>> {
      return ClassGraph()
         .enableClassInfo()
         .enableExternalClasses()
         .rejectPackages(
            "java.*",
            "javax.*",
            "sun.*",
            "com.sun.*",
            "kotlin.*",
            "kotlinx.*",
            "androidx.*",
            "org.jetbrains.kotlin.*",
            "org.junit.*",
         )
         .scan()
         .use { result ->
            result
               .getSubclasses(Spec::class.java.name)
               .filter { it.isStandardClass } // excludes interfaces and annotations
               .filterNot { it.isAbstract }   // only concrete specs can be instantiated
               .filterNot { it.isPrivate }    // private specs cannot be instantiated by the engine
               .map { Class.forName(it.name).kotlin }
               .filterIsInstance<KClass<out Spec>>()
         }
   }
}
