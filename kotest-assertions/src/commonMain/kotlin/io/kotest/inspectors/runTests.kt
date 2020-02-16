package io.kotest.inspectors

fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<ElementResult<T>> {
  return col.map {
    try {
      f(it)
      ElementPass(it)
    } catch (e: Throwable) {
      ElementFail(it, e)
    }
  }
}
