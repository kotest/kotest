package io.kotest.extensions.system

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.extensions.system.OverrideMode.SetOrError
import java.lang.reflect.Field

/**
 * Modifies System Environment with chosen key and value
 *
 * This is a helper function for code that uses Environment Variables. It changes the specific [key] from [System.getenv]
 * with the specified [value], only during the execution of [block].
 *
 * To do this, this function uses a trick that makes the System Environment editable, and changes [key]. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of [block], the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
inline fun <T> withEnvironment(key: String, value: String?, mode: OverrideMode = SetOrError, block: () -> T): T {
   return withEnvironment(key to value, mode, block)
}

/**
 * Modifies System Environment with chosen key and value
 *
 * This is a helper function for code that uses Environment Variables. It changes the specific key from [System.getenv]
 * with the specified value, only during the execution of [block].
 *
 * To do this, this function uses a trick that makes the System Environment editable, and changes key. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of [block], the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
inline fun <T> withEnvironment(environment: Pair<String, String?>, mode: OverrideMode = SetOrError, block: () -> T): T {
   return withEnvironment(mapOf(environment), mode, block)
}

/**
 * Modifies System Environment with chosen keys and values
 *
 * This is a helper function for code that uses Environment Variables. It changes the specific keys from [System.getenv]
 * with the specified values, only during the execution of [block].
 *
 * To do this, this function uses a trick that makes the System Environment editable, and changes key. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of [block], the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
inline fun <T> withEnvironment(environment: Map<String, String?>, mode: OverrideMode = SetOrError, block: () -> T): T {
   val isWindows = "windows" in System.getProperty("os.name").orEmpty().lowercase()
   val originalEnvironment = if (isWindows) {
      System.getenv().toSortedMap(String.CASE_INSENSITIVE_ORDER)
   } else {
      System.getenv().toMap()
   }

   setEnvironmentMap(mode.override(originalEnvironment, environment))

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

private fun Field.asAccessible(): Field {
   return apply { isAccessible = true }
}


abstract class SystemEnvironmentListener(private val environment: Map<String, String?>,
                                         private val mode: OverrideMode) {

   private val originalEnvironment = System.getenv().toMap()

   protected fun changeSystemEnvironment() {
      setEnvironmentMap(mode.override(originalEnvironment, environment))
   }

   protected fun resetSystemEnvironment() {
      setEnvironmentMap(originalEnvironment)
   }
}

/**
 * Modifies System Environment with chosen keys and values
 *
 * This is a Listener for code that uses Environment Variables. It changes the specific keys from [System.getenv]
 * with the specified values, only during the execution of a test.
 *
 * To do this, this listener uses a trick that makes the System Environment editable, and changes the keys. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of the test, the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
class SystemEnvironmentTestListener(environment: Map<String, String?>, mode: OverrideMode = SetOrError) :
   SystemEnvironmentListener(environment, mode), TestListener {

   /**
    * Modifies System Environment with chosen keys and values
    *
    * This is a Listener for code that uses Environment Variables. It changes the specific keys from [System.getenv]
    * with the specified values, only during the execution of a test.
    *
    * To do this, this listener uses a trick that makes the System Environment editable, and changes the keys. Any previous
    * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
    * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
    *
    * After the execution of the test, the environment is set to what it was before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Environment Map is a single map.
    */
   constructor(key: String, value: String?, mode: OverrideMode = SetOrError) : this(key to value, mode)

   /**
    * Modifies System Environment with chosen keys and values
    *
    * This is a Listener for code that uses Environment Variables. It changes the specific keys from [System.getenv]
    * with the specified values, only during the execution of a test.
    *
    * To do this, this listener uses a trick that makes the System Environment editable, and changes the keys. Any previous
    * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
    * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
    *
    * After the execution of the test, the environment is set to what it was before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Environment Map is a single map.
    */
   constructor(environment: Pair<String, String?>, mode: OverrideMode = SetOrError) : this(mapOf(environment), mode)

   override suspend fun beforeAny(testCase: TestCase) {
      changeSystemEnvironment()
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      resetSystemEnvironment()
   }
}

/**
 * Modifies System Environment with chosen keys and values
 *
 * This is a Listener for code that uses Environment Variables. It changes the specific keys from [System.getenv]
 * with the specified values, during the execution of the project.
 *
 * To do this, this listener uses a trick that makes the System Environment editable, and changes the keys. Any previous
 * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
 * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
 *
 * After the execution of the project, the environment is set to what it was before.
 *
 * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
 * already changed, the result is inconsistent, as the System Environment Map is a single map.
 */
class SystemEnvironmentProjectListener(environment: Map<String, String?>, mode: OverrideMode = SetOrError) :
   SystemEnvironmentListener(environment, mode), ProjectListener {


   /**
    * Modifies System Environment with chosen keys and values
    *
    * This is a Listener for code that uses Environment Variables. It changes the specific keys from [System.getenv]
    * with the specified values, during the execution of the project.
    *
    * To do this, this listener uses a trick that makes the System Environment editable, and changes the keys. Any previous
    * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
    * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
    *
    * After the execution of the project, the environment is set to what it was before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Environment Map is a single map.
    */
   constructor(key: String, value: String?, mode: OverrideMode = SetOrError) : this(key to value, mode)

   /**
    * Modifies System Environment with chosen keys and values
    *
    * This is a Listener for code that uses Environment Variables. It changes the specific keys from [System.getenv]
    * with the specified values, during the execution of the project.
    *
    * To do this, this listener uses a trick that makes the System Environment editable, and changes the keys. Any previous
    * environment (anything not overridden) will also be in the environment. If the chosen key is in the environment,
    * it will be changed according to [mode]. If the chosen key is not in the environment, it will be included.
    *
    * After the execution of the project, the environment is set to what it was before.
    *
    * **ATTENTION**: This code is susceptible to race conditions. If you attempt to change the environment while it was
    * already changed, the result is inconsistent, as the System Environment Map is a single map.
    */
   constructor(environment: Pair<String, String?>, mode: OverrideMode = SetOrError) : this(mapOf(environment), mode)

   override suspend fun beforeProject() {
      changeSystemEnvironment()
   }

   override suspend fun afterProject() {
      resetSystemEnvironment()
   }
}
