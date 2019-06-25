import io.kotlintest.properties.PropertyContext
import io.kotlintest.show.show

@Deprecated("Use the Show typeclass directly", ReplaceWith("value.show()", "io.kotlintest.show.Show"))
fun convertValueToString(value: Any?): String = value.show()

fun exceptionToMessage(t: Throwable): String =
  when (t) {
    is AssertionError -> when (t.message) {
      null -> t.toString()
      else -> t.message!!
    }
    else -> t.toString()
  }

fun outputClassifications(context: PropertyContext) {
  context.classificationCounts().entries.sortedByDescending { it.value }.forEach {
    val percentage = (it.value / context.attempts().toDouble() * 100)
    println("${String.format("%.2f", percentage)}% ${it.key}")
  }
}