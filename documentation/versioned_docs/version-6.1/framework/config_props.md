---
id: framework_config_props
title: Framework configuration properties
sidebar_label: System properties
slug: framework-config-props.html
---

---
#### KotestEngineProperties.kt
```kotlin
package io.kotest.core.internal

object KotestEngineProperties {

   const val scriptsEnabled = "kotest.framework.scripts.enabled"

   const val dumpConfig = "kotest.framework.dump.config"

   /**
    * Sets the tag expression that determines included/excluded tags.
    */
   const val tagExpression = "kotest.tags"

   const val excludeTags = "kotest.tags.exclude"

   const val includeTags = "kotest.tags.include"

   /**
    * A regex expression that is used to match the test [io.kotest.core.descriptors.Descriptor]'s path
    * to determine if a test should be included in the test plan or not.
    */
   const val filterTests = "kotest.filter.tests"

   /**
    * A regex expression that is used to match the [io.kotest.mpp.bestName] of a class
    * to determine if a spec should be included in the test plan or not.
    */
   const val filterSpecs = "kotest.filter.specs"

   const val propertiesFilename = "kotest.properties.filename"

   /**
    * If set to true, then source ref's will not be created for test cases.
    * This may speed up builds (as the engine will not need to create stack traces to
    * generate line numbers) but will also reduce functionality in the intellij plugin
    * (by limiting the ability to drill directly into the test inside a file).
    */
   const val disableSourceRef = "kotest.framework.sourceref.disable"

   /**
    * If set to true, disables the use of '!' as a prefix to disable tests.
    */
   const val disableBangPrefix = "kotest.bang.disable"

   /**
    * The default [io.kotest.core.spec.IsolationMode] for specs.
    */
   const val isolationMode = "kotest.framework.isolation.mode"

   /**
    * The default [io.kotest.core.test.AssertionMode] for tests.
    */
   const val assertionMode = "kotest.framework.assertion.mode"

   /**
    * The default parallelism for specs.
    */
   const val parallelism = "kotest.framework.parallelism"

   /**
    * The default timeout for test cases.
    */
   const val timeout = "kotest.framework.timeout"

   /**
    * The default timeout for the entire test suite.
    */
   const val projectTimeout = "kotest.framework.projecttimeout"

   const val logLevel = "kotest.framework.loglevel"

   /**
    * The default timeout for each invocation of a test case.
    */
   const val invocationTimeout = "kotest.framework.invocation.timeout"

   /**
    * Use classpath scanning for test discovery if no selectors are present (defaults to "false").
    * - Do not enable this when using Gradle with `maxParallelForks > 1`. Gradle might inadvertently invoke one
    *   Kotest instance with an empty class list, resulting in duplicate test runs (#3973).
    */
   const val discoveryClasspathFallbackEnabled = "kotest.framework.discovery.classpath.fallback.enabled"

   const val disableTestNestedJarScanning = "kotest.framework.disable.test.nested.jar.scanning"

   const val concurrentSpecs = "kotest.framework.spec.concurrent"

   const val concurrentTests = "kotest.framework.test.concurrent"

   /**
    * Disable scanning the classpath for configuration classes by setting this property to true
    */
   const val disableConfigurationClassPathScanning = "kotest.framework.classpath.scanning.config.disable"

   /**
    * Specify a fully qualified name to use for project config.
    * This class will be instantiated via reflection.
    */
   const val configurationClassName = "kotest.framework.config.fqn"

   /**
    * Disable scanning the classpath for listeners with @AutoScan by setting this property to true
    */
   const val disableAutoScanClassPathScanning = "kotest.framework.classpath.scanning.autoscan.disable"

   const val allowMultilineTestName = "kotest.framework.testname.multiline"

   /**
    *  If set -> filter testCases by this severity level and higher, else running all
    */
   const val testSeverity = "kotest.framework.test.severity"

   /**
    * Enable assert softly globally.
    * */
   const val globalAssertSoftly = "kotest.framework.assertion.globalassertsoftly"

   /**
    * Appends all tags associated with a test case to its display name.
    * */
   const val testNameAppendTags = "kotest.framework.testname.append.tags"

   /**
    * Controls whether classes will inherit tags from their supertypes. Default false
    */
   const val tagInheritance = "kotest.framework.tag.inheritance"

   /**
    * Controls the [io.kotest.core.names.DuplicateTestNameMode] mode.
    */
   const val duplicateTestNameMode = "kotest.framework.testname.duplicate.mode"

   const val disableJarDiscovery = "kotest.framework.discovery.jar.scan.disable"
}
```
