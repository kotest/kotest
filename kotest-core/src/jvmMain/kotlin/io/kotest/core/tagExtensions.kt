package io.kotest.core

import io.kotest.Project
import io.kotest.core.test.TestCaseFilter
import io.kotest.extensions.TagExtension
import io.kotest.extensions.TestCaseExtension

actual fun tagExtensions(): List<TagExtension> = Project.tagExtensions()
actual fun testCaseExtensions(): List<TestCaseExtension> = Project.testCaseExtensions()
actual fun testCaseFilters(): List<TestCaseFilter> = Project.testCaseFilters()
