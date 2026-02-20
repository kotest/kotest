package io.kotest.datatest

/**
 * Non-JVM implementation - we don't have access to stack traces on these platforms,
 * and therefore aren't able to get a Line Number.
 * Data tests here will all be annotated with `kotest.data` (default for all) and `kotest.data.nonJvm`
 * Either of these tags can potentially be used to run all data tests within a spec or a project.
 */
internal actual fun getDataTestCallSiteLineNumber(): String = "nonJvm"
