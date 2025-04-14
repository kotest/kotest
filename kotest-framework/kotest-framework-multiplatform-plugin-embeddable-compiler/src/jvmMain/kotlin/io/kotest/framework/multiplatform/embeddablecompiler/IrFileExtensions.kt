package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.ir.declarations.IrFile
import java.io.File

// These extension properties are available in org.jetbrains.kotlin.ir.declarations, but were moved from one file to
// another in Kotlin 1.7. This breaks backwards compatibility with earlier versions of Kotlin.
// So instead of using the provided implementations, we've copied them here, so we can work with both Kotlin 1.7+ and earlier
// versions without issue.
// See https://github.com/kotest/kotest/issues/3060 and https://youtrack.jetbrains.com/issue/KT-52888 for more information.
internal val IrFile.path: String get() = fileEntry.name
internal val IrFile.name: String get() = File(path).name
