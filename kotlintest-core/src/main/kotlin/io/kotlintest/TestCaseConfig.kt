package io.kotlintest

import java.time.Duration

data class TestCaseConfig(
    val enabled: Boolean = true,
    val invocations: Int = 1,
    val timeout: Duration = Duration.ZERO,
    val threads: Int = 1,
    val tags: Set<Tag> = setOf(),
    val interceptors: List<(TestCaseContext, () -> Unit) -> Unit> = listOf())