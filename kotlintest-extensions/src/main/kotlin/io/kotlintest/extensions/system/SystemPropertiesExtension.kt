package io.kotlintest.extensions.system

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import java.util.*

inline fun <T> withSystemProperty(key: String, value: String?, thunk: () -> T): T {
    val previous = System.setProperty(key, value)
    try {
        return thunk()
    } finally {
        if (previous == null)
            System.clearProperty(key)
        else
            System.setProperty(key, previous)
    }
}

inline fun <T> withSystemProperties(props: List<Pair<String, String?>>, thunk: () -> T): T {
    return withSystemProperties(props.toMap(), thunk)
}

inline fun <T> withSystemProperties(props: Properties, thunk: () -> T): T {
    val pairs = props.toList().map { it.first.toString() to it.second?.toString() }
    return withSystemProperties(pairs, thunk)
}

inline fun <T> withSystemProperties(props: Map<String, String?>, thunk: () -> T): T {
    val prevs = props.map { it.key to System.setProperty(it.key, it.value) }
    try {
        return thunk()
    } finally {
        prevs.forEach {
            if (it.second == null)
                System.clearProperty(it.first)
            else
                System.setProperty(it.first, it.second)
        }
    }
}

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