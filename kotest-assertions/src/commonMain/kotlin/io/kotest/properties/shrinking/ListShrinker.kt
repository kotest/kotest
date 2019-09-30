package io.kotest.properties.shrinking

class ListShrinker<T> : Shrinker<List<T>> {
  override fun shrink(failure: List<T>): List<List<T>> {
    return if (failure.isEmpty()) emptyList() else {
      listOf(
          emptyList(),
          failure.takeLast(1),
          failure.take(failure.size / 3),
          failure.take(failure.size / 2),
          failure.take(failure.size * 2 / 3),
          failure.dropLast(1)
      )
    }
  }
}