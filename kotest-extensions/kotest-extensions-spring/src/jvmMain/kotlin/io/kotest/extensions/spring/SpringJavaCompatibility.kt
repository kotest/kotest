package io.kotest.extensions.spring

import io.kotest.core.LogLine
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal object SpringJavaCompatibility {

   private val logger = Logger<SpringJavaCompatibility>()

   var ignoreSpringListenerOnFinalClassWarning: Boolean = false

   // methodHandle() is called multiple times per test case (intercept/beforeAny/afterAny). Spring's
   // TestContextManager only needs *a* Method belonging to the spec class - it doesn't need a fresh
   // one per call - so we generate the ByteBuddy subclass once per test case and cache it here rather
   // than re-generating and re-loading a new class into a new classloader on every call.
   private val fakeMethodCache = ConcurrentHashMap<Descriptor.TestDescriptor, Method>()

   private val fallbackMethodForFinalSpecs: Method by lazy {
      // the method here must exist since we can't add our own
      val methods = this@SpringJavaCompatibility::class.java.methods
      methods.firstOrNull { it.name == "methodHandle" }
         ?: error("Could not find a method to attach spring lifecycle methods to: ${methods.map { it.name }}")
   }

   /**
    * Generates a fake [Method] for the given [TestCase].
    *
    * Check https://github.com/kotest/kotest/issues/950#issuecomment-524127221
    * for an in-depth explanation. Too much to write here
    */
   fun methodHandle(testCase: TestCase): Method = if (Modifier.isFinal(testCase.spec::class.java.modifiers)) {
      if (!ignoreFinalWarning) {
         @Suppress("MaxLineLength")
         println("Using SpringExtension on a final class. If any Spring annotation fails to work, try making this class open.")
      }
      fallbackMethodForFinalSpecs
   } else {
      fakeMethodCache.getOrPut(testCase.descriptor) { generateFakeMethod(testCase) }
   }

   private fun generateFakeMethod(testCase: TestCase): Method {
      val methodName = methodName(testCase)
      val fakeSpec = ByteBuddy()
         .subclass(testCase.spec::class.java)
         .defineMethod(methodName, String::class.java, Visibility.PUBLIC)
         .intercept(FixedValue.value("Foo"))
         .make()
         .load(this::class.java.classLoader, ClassLoadingStrategy.Default.CHILD_FIRST)
         .loaded
      return fakeSpec.getMethod(methodName)
   }

   /**
    * Checks for a safe class name and throws if invalid
    * https://kotlinlang.org/docs/keyword-reference.html#soft-keywords
    */
   fun checkForSafeClassName(kclass: KClass<*>) {
      logger.log { LogLine(kclass, "Checking for spring safe class name") }
      // these are names java won't let us use but are ok from kotlin
      if (kclass.java.name.split('.').any { illegals.contains(it) })
         error("Spec package name cannot contain a java keyword: ${illegals.joinToString(",")}")
   }

   /**
    * Generates a fake method name for the given [TestCase].
    * The method name is taken from the test case name with a random element.
    */
   fun methodName(testCase: TestCase): String = (testCase.name.name + "_" + UUID.randomUUID().toString())
      .replace(methodNameRegex, "_")
      .let {
         if (it.first().isLetter()) it else "_$it"
      }

   private val illegals =
      listOf("import", "finally", "catch", "const", "final", "inner", "protected", "private", "public")

   private val methodNameRegex = "[^a-zA-Z_0-9]".toRegex()

   private val ignoreFinalWarning =
      ignoreSpringListenerOnFinalClassWarning ||
         System.getProperty(Properties.SPRING_IGNORE_WARNING, "false").toBoolean()
}
