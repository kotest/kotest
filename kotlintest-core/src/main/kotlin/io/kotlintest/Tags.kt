package io.kotlintest

data class Tags(val included: Set<Tag>, val excluded: Set<Tag>) {

  companion object {
    val Empty = Tags(emptySet(), emptySet())
  }

  fun isActive(tags: Set<Tag>): Boolean {
    val includedTagsEmpty = included.isEmpty()
    return when {
      excluded.intersect(tags).isNotEmpty() -> false
      includedTagsEmpty -> true
      included.intersect(tags).isNotEmpty() -> true
      else -> false
    }
  }

  fun combine(other: Tags) = Tags(included + other.included, excluded + other.excluded)
}