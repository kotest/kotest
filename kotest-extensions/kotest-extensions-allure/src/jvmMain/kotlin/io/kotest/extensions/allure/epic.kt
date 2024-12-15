package io.kotest.extensions.allure

import io.kotest.core.test.TestCase
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Owner
import io.qameta.allure.Severity
import io.qameta.allure.Story
import io.qameta.allure.model.Label
import io.qameta.allure.util.ResultsUtils
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

fun KClass<*>.epic(): Label? = findAnnotation<Epic>()?.let { ResultsUtils.createEpicLabel(it.value) }
fun KClass<*>.feature(): Label? = findAnnotation<Feature>()?.let { ResultsUtils.createFeatureLabel(it.value) }
fun KClass<*>.severity(): Label? = findAnnotation<Severity>()?.let { ResultsUtils.createSeverityLabel(it.value) }
fun KClass<*>.story(): Label? = this.findAnnotation<Story>()?.let { ResultsUtils.createStoryLabel(it.value) }
fun KClass<*>.owner(): Label? = this.findAnnotation<Owner>()?.let { ResultsUtils.createOwnerLabel(it.value) }
fun KClass<*>.description() = this.findAnnotation<io.qameta.allure.Description>()?.value

fun TestCase.epic(): Label? = spec::class.epic()
fun TestCase.feature(): Label? = spec::class.feature()
fun TestCase.story(): Label? = spec::class.story()
fun TestCase.owner(): Label? = spec::class.owner()
fun TestCase.description() = spec::class.description()
