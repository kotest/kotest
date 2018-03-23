package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

data class TestCaseConfig(
    val enabled: Boolean = true,
    val invocations: Int = 1,
    val timeout: Duration = Duration.ofSeconds(600),
    val threads: Int = 1,
    val tags: Set<Tag> = setOf(),
    val extensions: List<TestCaseExtension> = emptyList())