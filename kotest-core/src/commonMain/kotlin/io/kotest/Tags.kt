package io.kotest

data class Tags(val included: Set<Tag>, val excluded: Set<Tag>) {

  companion object {
    val Empty = Tags(emptySet(), emptySet())
    fun include(vararg tags: Tag): Tags = Tags(tags.toSet(), emptySet())
    fun exclude(vararg tags: Tag): Tags = Tags(emptySet(), tags.toSet())
  }

  fun isActive(tag: Tag): Boolean = isActive(setOf(tag))
  fun isActive(tags: Set<Tag>): Boolean {
    return when {
      excluded.map { it.name }.intersect(tags.map { it.name }).isNotEmpty() -> false
      included.isEmpty() -> true
      included.map { it.name }.intersect(tags.map { it.name }).isNotEmpty() -> true
      else -> false
    }
  }

  fun combine(other: Tags) = Tags(included + other.included, excluded + other.excluded)
}