package io.kotest.engine.config

object KotestEngineProperties {

   internal const val DUMP_CONFIG = "kotest.framework.dump.config"

   /**
    * Sets the tag expression that determines included/excluded tags.
    */
   internal const val TAG_EXPRESSION = "kotest.tags"

   internal const val excludeTags = "kotest.tags.exclude"

   internal const val includeTags = "kotest.tags.include"

   /**
    * A regex expression that is used to match the test [io.kotest.core.descriptors.Descriptor]'s path
    * to determine if a test should be included in the test plan or not.
    */
   internal const val filterTests = "kotest.filter.tests"

   /**
    * A regex expression that is used to match the [io.kotest.mpp.bestName] of a class
    * to determine if a spec should be included in the test plan or not.
    */
   internal const val filterSpecs = "kotest.filter.specs"

   /**
    * Specifies the name of a system property that is used to define a properties file to load, and
    * each property in that file is then in turn applied as a system property.
    */
   internal const val PROPERTIES_FILENAME = "kotest.properties.filename"

   /**
    * If set to true, then source ref's will not be created for test cases.
    * This may speed up builds (as the engine will not need to create stack traces to
    * generate line numbers) but will also reduce functionality in the intellij plugin
    * (by limiting the ability to drill directly into the test inside a file).
    */
   internal const val DISABLE_SOURCE_REF = "kotest.framework.sourceref.disable"

   /**
    * If set to true, disables the use of '!' as a prefix to disable tests.
    */
   internal const val DISABLE_BANG_PREFIX = "kotest.bang.disable"

   /**
    * The default [io.kotest.core.spec.IsolationMode] for specs.
    */
   internal const val ISOLATION_MODE = "kotest.framework.isolation.mode"

   /**
    * The default [io.kotest.core.test.AssertionMode] for tests.
    */
   internal const val ASSERTION_MODE = "kotest.framework.assertion.mode"

   /**
    * The default timeout for test cases.
    */
   const val TIMEOUT = "kotest.framework.timeout"

   /**
    * The default timeout for the entire test suite.
    */
   internal const val PROJECT_TIMEOUT = "kotest.framework.projecttimeout"

   internal const val logLevel = "kotest.framework.loglevel"

   /**
    * The default timeout for each invocation of a test case.
    */
   const val INVOCATION_TIMEOUT = "kotest.framework.invocation.timeout"

   internal const val DISPLAY_FULL_TEST_PATH = "kotest.framework.testname.display.full.path"

   /**
    * Specify a fully qualified name to use for project config.
    * This class will be instantiated via reflection.
    */
   const val PROJECT_CONFIGURATION_FQN = "kotest.framework.config.fqn"

   internal const val ALLOW_MULTILINE_TEST_NAME = "kotest.framework.testname.multiline"

   /**
    *  If set -> filter testCases by this severity level and higher, else running all
    */
   internal const val TEST_SEVERITY = "kotest.framework.test.severity"

   /**
    * Enable assert softly globally.
    * */
   internal const val GLOBAL_ASSERT_SOFTLY = "kotest.framework.assertion.globalassertsoftly"

   internal const val COROUTINE_DEBUG_PROBES = "kotest.framework.coroutine.debug.probes"

   /**
    * Appends all tags associated with a test case to its display name.
    * */
   internal const val TEST_NAME_APPEND_TAGS = "kotest.framework.testname.append.tags"

   /**
    * Controls whether classes will inherit tags from their supertypes. Default false
    */
   internal const val TAG_INHERITANCE = "kotest.framework.tag.inheritance"

   /**
    * Controls the [io.kotest.core.names.DuplicateTestNameMode] mode.
    */
   internal const val DUPLICATE_TEST_NAME_MODE = "kotest.framework.testname.duplicate.mode"

   /**
    * If set to true, then private classes will not be included in the test plan.
    */
   internal const val IGNORE_PRIVATE_CLASSES = "kotest.framework.discovery.ignore.private.classes"
}
