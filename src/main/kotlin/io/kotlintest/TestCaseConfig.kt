package io.kotlintest

data class TestCaseConfig(
    val enabled: Boolean = true,
    val invocations: Int = 1,
    val timeout: Duration = unlimited,
    val threads: Int = 1,
    val tags: Set<Tag> = setOf(),
    val interceptors: List<(TestCaseContext, () -> Unit) -> Unit> = listOf())