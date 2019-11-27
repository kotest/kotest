package com.sksamuel.kotest.properties

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContainExactlyInAnyOrder
import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.lines
import io.kotest.specs.ShouldSpec

class GenLinesTest : ShouldSpec() {
    
    
    init {
        should("Generate from lines input stream") {
            val inputStream = List(4) { "line$it" }.joinToString(separator = "\n").byteInputStream()
            
            val lines = mutableSetOf<String>()
            Gen.lines(inputStream).assertAll { line ->
                lines += line
            }
            lines.shouldContainExactlyInAnyOrder("line0", "line1", "line2", "line3")
        }
    
        should("Generate random different values and not sequenced") {
            val inputStream = List(10000) { "$it" }.joinToString(separator = "\n").byteInputStream()
            val lines = mutableListOf<String>()
            Gen.lines(inputStream).assertAll(10000) { line ->
                lines += line
            }
            lines shouldNotContainExactlyInAnyOrder MutableList(10000) { "$it" }
        }
    }
}
