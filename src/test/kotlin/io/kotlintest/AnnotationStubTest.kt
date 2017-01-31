package io.kotlintest

import io.kotlintest.TestBase.Companion.a
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class AnnotationStubTest {
  @Test
  fun `test Annotation helper with map-like arguments`() {
    val deprecationMessage = "deprecation message"
    val replacementExpression = "test replacement expression"
    val annotation = a<Deprecated>(
        "message" to deprecationMessage,
        "replaceWith" to a<ReplaceWith>(
            "expression" to replacementExpression
        )
    )

    assertThat(annotation, instanceOf(Deprecated::class.java))
    assertThat(annotation.message, equalTo(deprecationMessage))
    assertThat(annotation.level, equalTo(DeprecationLevel.WARNING))
    assertThat(annotation.replaceWith, instanceOf(ReplaceWith::class.java))
    assertThat(annotation.replaceWith.expression, equalTo(replacementExpression))
    assertThat(annotation.replaceWith.imports.isEmpty(), equalTo(true))
  }

  @Test
  fun `test AnnotationHelper with no arguments`() {
    val annotation = a<Test>()

    assertThat(annotation, instanceOf(Test::class.java))
    assertThat(annotation.expected, notNullValue())
    assertEquals(annotation.expected, Test.None::class.java)
    assertThat(annotation.timeout, equalTo(0L))
  }

  @Test
  fun `test AnnotationHelper with single argument`() {
    val ignoreMessage = "ignore message"
    val annotation = a<Ignore>(ignoreMessage)

    assertThat(annotation, instanceOf(Ignore::class.java))
    assertThat(annotation.value, equalTo(ignoreMessage))
  }

  @Test
  fun `test AnnotationHelper #equals`() {
    val annotation = a<Ignore>()
    val annotationToCompareWith = a<Ignore>()

    assertThat(annotation, equalTo(annotationToCompareWith))
  }

  @Test
  fun `test AnnotationHelper #toString`() {

    @Deprecated(message = "testMessage") class ClassToExtractRealAnnotation

    val expectedAnnotation = ClassToExtractRealAnnotation::class.annotations.first()
    val actual = a<Deprecated>("message" to "testMessage")

    // Can't just use equals, cause result strings have different order of properties
    assertThat("Strings are similar", 0, equalTo((actual.toString().toSet() - expectedAnnotation.toString().toSet()).size))
  }
}