package io.kotest.extensions.allure

import io.kotest.core.test.TestCase
import io.qameta.allure.Issue
import io.qameta.allure.Links
import io.qameta.allure.model.Link
import io.qameta.allure.util.ResultsUtils
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import io.qameta.allure.Link as ALink

fun KClass<*>.issue() = this.findAnnotation<Issue>()?.let { ResultsUtils.createIssueLink(it.value) }
fun KClass<*>.link() = this.findAnnotation<ALink>()?.let { ResultsUtils.createLink(it) }
fun KClass<*>.links() = findAnnotation<Links>()?.value?.toList()?.map { ResultsUtils.createLink(it) } ?: emptyList()

fun TestCase.link(): Link? = spec::class.link()
fun TestCase.issue(): Link? = spec::class.issue()
fun TestCase.links(): List<Link> = spec::class.links()
