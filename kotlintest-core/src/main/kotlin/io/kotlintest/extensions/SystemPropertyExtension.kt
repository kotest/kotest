package io.kotlintest.extensions

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import java.util.*

class SystemPropertyExtension(private val props: Properties) : TestCaseExtension {

  constructor(key: String, value: String?) : this(Properties()) {
    props.setProperty(key, value)
  }

  constructor(map: Map<String, String?>) : this(Properties()) {
    map.forEach { props.setProperty(it.key, it.value) }
  }

  private suspend fun <T> withSystemProperties(props: Properties, thunk: suspend () -> T): T {
    val prevs = props.toList().map { it.first to System.setProperty(it.first.toString(), it.second?.toString()) }
    try {
      return thunk()
    } finally {
      prevs.forEach {
        if (it.second == null)
          System.clearProperty(it.first.toString())
        else
          System.setProperty(it.first.toString(), it.second)
      }
    }
  }

  override suspend fun intercept(testCase: TestCase,
                                 execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                 complete: suspend (TestResult) -> Unit) {
    withSystemProperties(props) {
      execute(testCase) { complete(it) }
    }
  }
}