package io.kotest.extensions.system

import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.extensions.system.OverrideMode.SetOrError
import java.util.Properties

/**
 * Changes System Properties with chosen key and value
 *
 * This is a helper function for code that uses System Properties. It changes the specific [key] from [System.getProperties]
 * with the specified [value], only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
 * properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperty(key: String, value: String?, mode: OverrideMode = SetOrError, block: () -> T): T {
   return withSystemProperties(key to value, mode, block)
}

/**
 * Changes System Properties with chosen key and value
 *
 * This is a helper function for code that uses System Properties. It changes the specific key from [System.getProperties]
 * with the specified value, only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
 * properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperties(pair: Pair<String, String?>, mode: OverrideMode = SetOrError, block: () -> T): T {
   return withSystemProperties(mapOf(pair), mode, block)
}

/**
 * Changes System Properties with chosen properties
 *
 * This is a helper function for code that uses System Properties. It changes the specific keys from [System.getProperties]
 * with the specified values, only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
 * properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperties(props: Properties, mode: OverrideMode = SetOrError, block: () -> T): T {
   val map = props.toStringStringMap()
   return withSystemProperties(map, mode, block)
}

/**
 * Changes System Properties with chosen keys and values
 *
 * This is a helper function for code that uses System Properties. It changes the specific key from [System.getProperties]
 * with the specified value, only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
 * properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperties(props: Map<String, String?>, mode: OverrideMode = SetOrError, block: () -> T): T {
   val previous =
      Properties().apply { putAll(System.getProperties()) }.toStringStringMap()  // Safe copying to ensure immutability

   setSystemProperties(mode.override(previous, props))

   try {
      return block()
   } finally {
      setSystemProperties(previous)
   }
}

@PublishedApi
internal fun Properties.toStringStringMap(): Map<String, String> {
   return this.map { it.key.toString() to it.value.toString() }.toMap()
}

@PublishedApi
internal fun setSystemProperties(map: Map<String, String>) {
   val propertiesToSet = Properties().apply { putAll(map) }
   System.setProperties(propertiesToSet)
}


abstract class SystemPropertyListener(
   private val newProperties: Map<String, String?>,
   private val mode: OverrideMode
) {

   private val originalProperties = System.getProperties().toStringStringMap()

   protected fun changeSystemProperties() {
      setSystemProperties(mode.override(originalProperties, newProperties))
   }

   protected fun resetSystemProperties() {
      setSystemProperties(originalProperties)
   }
}

/**
 * Changes System Properties with chosen keys and values
 *
 * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
 * with the specified values, only during the execution of a test.
 *
 * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
 * properties, it will be included.
 *
 * After the execution of the test, the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
class SystemPropertyTestListener(newProperties: Map<String, String?>, mode: OverrideMode = SetOrError) :
   SystemPropertyListener(newProperties, mode), TestListener {

   /**
    * Changes System Properties with chosen keys and values
    *
    * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
    * with the specified values, only during the execution of a test.
    *
    * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
    * properties, it will be included.
    *
    * After the execution of the test, the properties are set to what they were before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Properties Map is a single map.
    */
   constructor(listOfPairs: List<Pair<String, String?>>, mode: OverrideMode = SetOrError) : this(
      listOfPairs.toMap(),
      mode
   )

   /**
    * Changes System Properties with chosen keys and values
    *
    * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
    * with the specified values, only during the execution of a test.
    *
    * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
    * properties, it will be included.
    *
    * After the execution of the test, the properties are set to what they were before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Properties Map is a single map.
    */
   constructor(key: String, value: String?, mode: OverrideMode = SetOrError) : this(mapOf(key to value), mode)

   /**
    * Changes System Properties with chosen keys and values
    *
    * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
    * with the specified values, only during the execution of a test.
    *
    * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
    * properties, it will be included.
    *
    * After the execution of the test, the properties are set to what they were before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Properties Map is a single map.
    */
   constructor(properties: Properties, mode: OverrideMode = SetOrError) : this(properties.toStringStringMap(), mode)

   override suspend fun beforeAny(testCase: TestCase) {
      changeSystemProperties()
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      resetSystemProperties()
   }
}

/**
 * Changes System Properties with chosen keys and values
 *
 * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
 * with the specified values, only during the execution of a test.
 *
 * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
 * properties, it will be included.
 *
 * After the execution of the test, the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
class SystemPropertyProjectListener(newProperties: Map<String, String?>, mode: OverrideMode = SetOrError) :
   SystemPropertyListener(newProperties, mode), ProjectListener {

   /**
    * Changes System Properties with chosen keys and values
    *
    * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
    * with the specified values, only during the execution of a test.
    *
    * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
    * properties, it will be included.
    *
    * After the execution of the test, the properties are set to what they were before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Properties Map is a single map.
    */
   constructor(listOfPairs: List<Pair<String, String?>>, mode: OverrideMode = SetOrError) : this(
      listOfPairs.toMap(),
      mode
   )

   /**
    * Changes System Properties with chosen keys and values
    *
    * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
    * with the specified values, only during the execution of a test.
    *
    * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
    * properties, it will be included.
    *
    * After the execution of the test, the properties are set to what they were before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Properties Map is a single map.
    */
   constructor(key: String, value: String?, mode: OverrideMode = SetOrError) : this(mapOf(key to value), mode)

   /**
    * Changes System Properties with chosen keys and values
    *
    * This is a Listener for code that uses System Properties. It changes the specific keys from [System.getProperties]
    * with the specified values, only during the execution of a test.
    *
    * If the chosen key is in the properties, it will be changed according to [mode]. If the chosen key is not in the
    * properties, it will be included.
    *
    * After the execution of the test, the properties are set to what they were before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Properties Map is a single map.
    */
   constructor(properties: Properties, mode: OverrideMode = SetOrError) : this(properties.toStringStringMap(), mode)

   override suspend fun beforeProject() {
      changeSystemProperties()
   }

   override suspend fun afterProject() {
      resetSystemProperties()
   }
}
