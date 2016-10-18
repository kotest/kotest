package io.kotlintest

data class TestCaseConfig(
    val ignored: Boolean = false,
    val invocations: Int = 1,
    val timeout: Duration = Duration.unlimited,
    val threads: Int = 1,
    val tags: Set<Tag> = setOf(),
    val interceptors: Iterable<(TestCaseContext, () -> Unit) -> Unit> = listOf())