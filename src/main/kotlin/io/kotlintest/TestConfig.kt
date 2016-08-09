package io.kotlintest

class TestConfig(
    val ignored: Boolean = false,
    val invocations: Int = 1,
    val timeout: Duration = Duration.unlimited,
    val threads: Int = 1,
    tags: Set<Tag> = setOf(),
    tag: Tag? = null) {

  val tags = if (tag != null) tags + tag else tags

  fun copy(
      ignored: Boolean = this.ignored,
      invocations: Int = this.invocations,
      timeout: Duration = this.timeout,
      threads: Int = this.threads,
      tags: Set<Tag> = this.tags) = TestConfig(ignored, invocations, timeout, threads, tags)
}