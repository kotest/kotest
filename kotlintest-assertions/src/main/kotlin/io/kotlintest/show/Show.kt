package io.kotlintest.show

import java.util.*
import kotlin.reflect.full.memberProperties

fun Any?.show() = Show(this).show(this)

interface Show<in A> {
  fun show(a: A): String
  fun supports(a: Any?): Boolean

  @Suppress("UNCHECKED_CAST")
  companion object {

    private fun <T> fromServiceLoader(t: T): Show<T>? =
        ServiceLoader.load(Show::class.java).toList().find { it.supports(t) } as? Show<T>

    private fun <T> forDataClass(t: T): Show<T>? =
        if ((t as? Any)?.javaClass?.kotlin?.isData == true) DataClassShow as Show<T> else null

    operator fun <T> invoke(t: T): Show<T> = when (t) {
      null -> NullShow as Show<T>
      is String -> StringShow as Show<T>
      else -> fromServiceLoader(t) ?: AnyShow
    }
  }
}

object StringShow : Show<String> {
  override fun supports(a: Any?): Boolean = a is String
  override fun show(a: String): String = when (a) {
    "" -> "<empty string>"
    else -> if (a.isBlank()) a.replace("\n", "\\n").replace("\t", "\\t").replace(" ", "\\s") else a
  }
}

object AnyShow : Show<Any?> {
  override fun supports(a: Any?): Boolean = true
  override fun show(a: Any?): String = when (a) {
    null -> "<null>"
    else -> a.toString()
  }
}

object DataClassShow : Show<Any> {
  override fun supports(a: Any?): Boolean = a?.javaClass?.kotlin?.isData ?: false
  override fun show(a: Any): String {
    val klass = a.javaClass.kotlin
    require(klass.isData)
    return "${klass.simpleName}(\n" +
        klass.memberProperties.joinToString("\n") {
          "- ${it.name}: ${it.get(a)}"
        } + "\n)"
  }
}

object NullShow : Show<Nothing?> {
  override fun supports(a: Any?): Boolean = a == null
  override fun show(a: Nothing?): String = "<null>"
}