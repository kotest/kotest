package io.kotest.runner.junit5

import io.kotest.core.specs.SpecContainer
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FileSource
import java.io.File

internal fun SpecContainer.testSource() = when (this) {
   is SpecContainer.ClassSpec -> ClassSource.from(this.kclass.java)
   is SpecContainer.ValueSpec -> FileSource.from(File(this.path))
}
