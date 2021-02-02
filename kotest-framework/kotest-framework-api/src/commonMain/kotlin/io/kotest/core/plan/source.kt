package io.kotest.core.plan

sealed class Source {

   abstract val filename: String

   data class ClassSource(override val filename: String) : Source()
   data class TestSource(override val filename: String, val lineNumber: Int) : Source()
}
