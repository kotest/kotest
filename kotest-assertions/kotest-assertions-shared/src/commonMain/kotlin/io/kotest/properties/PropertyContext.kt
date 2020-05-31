package io.kotest.properties

/**
 * A [PropertyContext] is used when executing a propery test.
 * It allows feedback and tracking of the state of the property test.
 */
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
class PropertyContext {

  private var values = mutableListOf<Any?>()
  private var attempts = 0
  private val counts = mutableMapOf<String, Int>()

  internal fun inc() {
    attempts++
  }

  fun attempts(): Int = attempts

  fun addValue(any: Any?) = values.add(any)

  fun values() = values.toList()

  fun classificationCounts(): Map<String, Int> = counts.toMap()

  fun classify(condition: Boolean, trueLabel: String) {
    if (condition) {
      val current = counts.getOrElse(trueLabel) { 0 }
      counts[trueLabel] = current + 1
    }
  }

  fun classify(condition: Boolean, trueLabel: String, falseLabel: String) {
    if (condition) {
      classify(condition, trueLabel)
    } else {
      classify(!condition, falseLabel)
    }
  }
}
