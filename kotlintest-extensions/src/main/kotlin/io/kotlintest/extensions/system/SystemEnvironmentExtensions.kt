package io.kotlintest.extensions.system

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import java.lang.reflect.Field

/**
 * Overrides System Environment with chosen key and value
 *
 * This is a helper function for code that uses Environment Variables. It overrides the specific [key] from [System.getenv]
 * with the specified [value], only during the execution of [block].
 *
 * To do this, this function uses a trick that makes the System Environment editable, and overrides [key]. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be overridden. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of [block], the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
inline fun <T> withEnvironment(key: String, value: String?, block: () -> T): T {
  return withEnvironment(key to value, block)
}

/**
 * Overrides System Environment with chosen key and value
 *
 * This is a helper function for code that uses Environment Variables. It overrides the specific key from [System.getenv]
 * with the specified value, only during the execution of [block].
 *
 * To do this, this function uses a trick that makes the System Environment editable, and overrides the key. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be overridden. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of [block], the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
inline fun <T> withEnvironment(environment: Pair<String, String?>, block: () -> T): T {
  return withEnvironment(mapOf(environment), block)
}

/**
 * Overrides System Environment with chosen keys and values
 *
 * This is a helper function for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
 * with the specified values, only during the execution of [block].
 *
 * To do this, this function uses a trick that makes the System Environment editable, and overrides the keys. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be overridden. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of [block], the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
inline fun <T> withEnvironment(environment: Map<String, String?>, block: () -> T): T {
  val originalEnvironment = System.getenv().toMap() // Using to map to guarantee it's not modified
  
  setEnvironmentMap(originalEnvironment overridenWith environment)
  
  try {
    return block()
  } finally {
    setEnvironmentMap(originalEnvironment)
  }
}

@PublishedApi
// Implementation inspired from https://github.com/stefanbirkner/system-rule
internal fun setEnvironmentMap(map: Map<String, String?>) {
  val envMapOfVariables = getEditableMapOfVariables()
  val caseInsensitiveEnvironment = getCaseInsensitiveEnvironment()
  
  envMapOfVariables.clear()
  caseInsensitiveEnvironment?.clear()
  
  envMapOfVariables.putReplacingNulls(map)
  caseInsensitiveEnvironment?.putReplacingNulls(map)
}

@Suppress("UNCHECKED_CAST")
private fun getEditableMapOfVariables(): MutableMap<String, String> {
  val systemEnv = System.getenv()
  val classOfMap = systemEnv::class.java
  
  return classOfMap.getDeclaredField("m").asAccessible().get(systemEnv) as MutableMap<String, String>
}

@Suppress("UNCHECKED_CAST")
private fun getCaseInsensitiveEnvironment(): MutableMap<String, String>? {
  val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
  
  return try {
    processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment").asAccessible().get(null) as MutableMap<String, String>?
  } catch (e: NoSuchFieldException) {
    // Only available in Windows, ok to return null if it's not found
    null
  }
}

private fun  Field.asAccessible(): Field {
  return apply { isAccessible = true }
}


abstract class SystemEnvironmentListener(private val environment: Map<String, String>) : TestListener {
  
  private val originalEnvironment = System.getenv().toMap()
  
  protected fun changeSystemEnvironment() {
    setEnvironmentMap(originalEnvironment overridenWith environment)
  }
  
  protected fun resetSystemEnvironment() {
    setEnvironmentMap(originalEnvironment)
  }
}

/**
 * Overrides System Environment with chosen keys and values
 *
 * This is a Listener for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
 * with the specified values, only during the execution of a test.
 *
 * To do this, this listener uses a trick that makes the System Environment editable, and overrides the keys. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be overridden. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of the test, the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
class SystemEnvironmentTestListener(environment: Map<String, String>) : SystemEnvironmentListener(environment) {
  
  /**
   * Overrides System Environment with chosen keys and values
   *
   * This is a Listener for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
   * with the specified values, only during the execution of a test.
   *
   * To do this, this listener uses a trick that makes the System Environment editable, and overrides the keys. Any previous
   * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
   * it will be overridden. If the chosen key is not in the environment, it will be included.
   *
   * After the execution of the test, the environment is set to what it was before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Environment Map is a single map.
   */
  constructor(key: String, value: String) : this(key to value)
  
  /**
   * Overrides System Environment with chosen keys and values
   *
   * This is a Listener for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
   * with the specified values, only during the execution of a test.
   *
   * To do this, this listener uses a trick that makes the System Environment editable, and overrides the keys. Any previous
   * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
   * it will be overridden. If the chosen key is not in the environment, it will be included.
   *
   * After the execution of the test, the environment is set to what it was before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Environment Map is a single map.
   */
  constructor(environment: Pair<String, String>) : this(mapOf(environment))
  
  override fun beforeTest(testCase: TestCase) {
    changeSystemEnvironment()
  }
  
  override fun afterTest(testCase: TestCase, result: TestResult) {
    resetSystemEnvironment()
  }
}

/**
 * Overrides System Environment with chosen keys and values
 *
 * This is a Listener for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
 * with the specified values, during the execution of the project.
 *
 * To do this, this listener uses a trick that makes the System Environment editable, and overrides the keys. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be overridden. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of the project, the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
class SystemEnvironmentProjectListener(environment: Map<String, String>) : SystemEnvironmentListener(environment) {
  
  
  /**
   * Overrides System Environment with chosen keys and values
   *
   * This is a Listener for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
   * with the specified values, during the execution of the project.
   *
   * To do this, this listener uses a trick that makes the System Environment editable, and overrides the keys. Any previous
   * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
   * it will be overridden. If the chosen key is not in the environment, it will be included.
   *
   * After the execution of the project, the environment is set to what it was before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Environment Map is a single map.
   */
  constructor(key: String, value: String) : this(key to value)
  
  /**
   * Overrides System Environment with chosen keys and values
   *
   * This is a Listener for code that uses Environment Variables. It overrides the specific keys from [System.getenv]
   * with the specified values, during the execution of the project.
   *
   * To do this, this listener uses a trick that makes the System Environment editable, and overrides the keys. Any previous
   * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
   * it will be overridden. If the chosen key is not in the environment, it will be included.
   *
   * After the execution of the project, the environment is set to what it was before.
   *
   * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
   * already changed, the result is inconsistent, as the System Environment Map is a single map.
   */
  constructor(environment: Pair<String, String>) : this(mapOf(environment))
  
  
  override fun beforeProject() {
    changeSystemEnvironment()
  }
  
  override fun afterProject() {
    resetSystemEnvironment()
  }
}