package io.kotest.core

import io.kotest.extensions.TagExtension
import io.kotest.extensions.TestCaseExtension

actual fun tagExtensions(): List<TagExtension> = emptyList()
actual fun testCaseExtensions(): List<TestCaseExtension> = emptyList()
actual fun testCaseFilters(): List<TestCaseFilter> = emptyList()
