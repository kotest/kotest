package io.kotlintest.extensions.system

import io.kotlintest.TestCase
import io.kotlintest.listener.TestListener
import io.kotlintest.TestResult
import java.util.*

/**
 * Overrides System Properties with chosen key and value
 *
 * This is a helper function for code that uses System Properties. It overrides the specific [key] from [System.getProperties]
 * with the specified [value], only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperty(key: String, value: String?, block: () -> T): T {
  return withSystemProperties(key to value, block)
}

/**
 * Overrides System Properties with chosen key and value
 *
 * This is a helper function for code that uses System Properties. It overrides the specific key from [System.getProperties]
 * with the specified value, only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperties(pair: Pair<String, String?>, block: () -> T): T {
  return withSystemProperties(mapOf(pair), block)
}

/**
 * Overrides System Properties with chosen properties
 *
 * This is a helper function for code that uses System Properties. It overrides the specific keys from [System.getProperties]
 * with the specified values, only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperties(props: Properties, block: () -> T): T {
  val map = props.toStringStringMap()
  return withSystemProperties(map, block)
}

/**
 * Overrides System Properties with chosen keys and values
 *
 * This is a helper function for code that uses System Properties. It overrides the specific key from [System.getProperties]
 * with the specified value, only during the execution of [block].
 *
 * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties, it will be included.
 *
 * After the execution of [block], the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the properties while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
inline fun <T> withSystemProperties(props: Map<String, String?>, block: () -> T): T {
  val previous = Properties().apply { putAll(System.getProperties()) }.toStringStringMap()  // Safe copying to ensure immutability
  
  setSystemProperties(previous overridenWith props)
  
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


abstract class SystemPropertyListener(private val newProperties: Map<String, String?>) : TestListener {
  
  private val originalProperties = System.getProperties().toStringStringMap()
  
  protected fun changeSystemProperties() {
    setSystemProperties(originalProperties overridenWith newProperties)
  }
  
  protected fun resetSystemProperties() {
    setSystemProperties(originalProperties)
  }
}

/**
 * Overrides System Properties with chosen keys and values
 *
 * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
 * with the specified values, only during the execution of a test.
 *
 * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
 * it will be included.
 *
 * After the execution of the test, the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
class SystemPropertyTestListener(newProperties: Map<String, String?>) : SystemPropertyListener(newProperties) {
  
  /**
   * Overrides System Properties with chosen keys and values
   *
   * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
   * with the specified values, only during the execution of a test.
   *
   * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
   * it will be included.
   *
   * After the execution of the test, the properties are set to what they were before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Properties Map is a single map.
   */
  constructor(listOfPairs: List<Pair<String, String?>>) : this(listOfPairs.toMap())
  
  /**
   * Overrides System Properties with chosen keys and values
   *
   * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
   * with the specified values, only during the execution of a test.
   *
   * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
   * it will be included.
   *
   * After the execution of the test, the properties are set to what they were before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Properties Map is a single map.
   */
  constructor(key: String, value: String?) : this(mapOf(key to value))
  
  /**
   * Overrides System Properties with chosen keys and values
   *
   * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
   * with the specified values, only during the execution of a test.
   *
   * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
   * it will be included.
   *
   * After the execution of the test, the properties are set to what they were before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Properties Map is a single map.
   */
  constructor(properties: Properties) : this(properties.toStringStringMap())
  
  override fun beforeTest(testCase: TestCase) {
    changeSystemProperties()
  }
  
  override fun afterTest(testCase: TestCase, result: TestResult) {
    resetSystemProperties()
  }
}

/**
 * Overrides System Properties with chosen keys and values
 *
 * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
 * with the specified values, only during the execution of a test.
 *
 * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
 * it will be included.
 *
 * After the execution of the test, the properties are set to what they were before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Properties Map is a single map.
 */
class SystemPropertyProjectListener(newProperties: Map<String, String?>) : SystemPropertyListener(newProperties) {
  
  /**
   * Overrides System Properties with chosen keys and values
   *
   * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
   * with the specified values, only during the execution of a test.
   *
   * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
   * it will be included.
   *
   * After the execution of the test, the properties are set to what they were before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Properties Map is a single map.
   */
  constructor(listOfPairs: List<Pair<String, String?>>) : this(listOfPairs.toMap())
  
  /**
   * Overrides System Properties with chosen keys and values
   *
   * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
   * with the specified values, only during the execution of a test.
   *
   * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
   * it will be included.
   *
   * After the execution of the test, the properties are set to what they were before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Properties Map is a single map.
   */
  constructor(key: String, value: String?) : this(mapOf(key to value))
  
  /**
   * Overrides System Properties with chosen keys and values
   *
   * This is a Listener for code that uses System Properties. It overrides the specific keys from [System.getProperties]
   * with the specified values, only during the execution of a test.
   *
   * If the chosen key is in the properties, it will be overridden. If the chosen key is not in the properties,
   * it will be included.
   *
   * After the execution of the test, the properties are set to what they were before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Properties Map is a single map.
   */
  constructor(properties: Properties) : this(properties.toStringStringMap())
  
  override fun beforeProject() {
    changeSystemProperties()
  }
  
  override fun afterProject() {
    resetSystemProperties()
  }
}