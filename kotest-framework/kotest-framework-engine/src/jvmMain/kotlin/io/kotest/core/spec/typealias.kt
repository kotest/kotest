package io.kotest.core.spec

import io.kotest.core.TestConfiguration
import io.kotest.engine.spec.tempfile
import java.io.File

@Deprecated(
   "This function has moved to io.kotest.engine.spec - this alias will be removed in 4.4",
   ReplaceWith("this.tempfile(prefix, suffix)", "io.kotest.engine.spec.tempfile")
)
fun TestConfiguration.tempfile(prefix: String? = null, suffix: String? = ".tmp"): File = this.tempfile(prefix, suffix)
