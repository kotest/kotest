package com.sksamuel.kotest.tests.json

import io.kotest.core.TestConfiguration
import io.kotest.engine.spec.tempfile

fun TestConfiguration.withJsonTestFile(content: String) = tempfile(suffix = ".json").also { it.writeText(content) }
