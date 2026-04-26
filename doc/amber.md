● Here's the full picture of how IntelliJ launches tests with the Amper build system:

---
Overview

The Amper plugin itself lives outside this repository, but intellij-community contains all the platform infrastructure it builds on. There are two Amper modes: Gradle-backed (Amper wrapping Gradle) and standalone Amper — they differ
mainly in console management and test output protocol.

---
Full Execution Path

User clicks "Run test" gutter icon
↓
RunConfigurationProducer.setupConfigurationFromContext()
→ KotlinJvmTestClassGradleConfigurationProducer (for JVM modules)
plugins/kotlin/gradle/gradle-java/src/…/KotlinJvmTestClassGradleConfigurationProducer.kt
→ sets GradleRunConfiguration with:
taskNames = [":test"]
scriptParameters = "--tests fully.qualified.ClassName"
isRunAsTest = true
↓
ExecutionManager.executeConfiguration()
↓
ExternalSystemRunConfiguration.getState()
→ ExternalSystemRunnableState
platform/external-system-impl/…/ExternalSystemRunnableState.java:218
↓
ExternalSystemRunnableState.execute()
→ getConsoleManagerFor(task)  (line 231)
→ iterates ExternalSystemExecutionConsoleManager.EP_NAME extensions
→ Gradle-backed Amper → GradleTestsExecutionConsoleManager
→ Standalone Amper    → AmperTestsExecutionConsoleManager (in Amper plugin)
↓
consoleManager.attachExecutionConsole()
→ creates SMTRunnerConsoleView
→ SMTestRunnerConnectionUtil.initConsoleView()
→ attaches output processor/converter
↓
Task runs; onTaskOutput() → consoleManager.onOutput() → parses test events
→ Gradle:           XML <ijLog>...</ijLog> events
→ Standalone Amper: ##teamcity[...] service messages
↓
SMTestProxy tree updated in UI

---
Key Differences: Gradle vs Standalone Amper

┌──────────────────────┬────────────────────────────────────────────┬─────────────────────────────────────────────────┐
│        Aspect        │                   Gradle                   │                Standalone Amper                 │
├──────────────────────┼────────────────────────────────────────────┼─────────────────────────────────────────────────┤
│ Run configuration    │ GradleRunConfiguration                     │ Own ExternalSystemRunConfiguration subclass     │
├──────────────────────┼────────────────────────────────────────────┼─────────────────────────────────────────────────┤
│ Console manager      │ GradleTestsExecutionConsoleManager         │ Own ExternalSystemExecutionConsoleManager       │
├──────────────────────┼────────────────────────────────────────────┼─────────────────────────────────────────────────┤
│ Test output protocol │ XML <ijLog> blocks                         │ TeamCity Service Messages (##teamcity[...])     │
├──────────────────────┼────────────────────────────────────────────┼─────────────────────────────────────────────────┤
│ Output converter     │ GradleTestsExecutionConsoleOutputProcessor │ OutputToGeneralTestEventsConverter (subclassed) │
└──────────────────────┴────────────────────────────────────────────┴─────────────────────────────────────────────────┘

---
Platform APIs Added Specifically for Amper

1. OutputToGeneralTestEventsConverter.handleUnexpectedServiceMessage() (AMPER-4202)
   platform/smRunner/src/…/OutputToGeneralTestEventsConverter.java:333
   Amper overrides this to silently drop flowStarted/flowFinished TeamCity messages that aren't part of the standard test protocol.
2. SettingsEditorFragment.isAvailable() (AMPER-4063)
   platform/platform-api/src/…/SettingsEditorFragment.java:317
   Lets Amper reactively show/hide run config editor fragments.
3. GradlePreviewCustomizer EP (AMPER-248)
   plugins/gradle/src/…/GradlePreviewCustomizer.kt:18
   Lets Amper customize Gradle project resolution preview.

  ---
Task Discovery (how test task names are found)

For Gradle-backed Amper, task names like :test come from GradleTestTasksProvider. For multiplatform modules, they're stored in KotlinFacetSettings.externalSystemRunTasks, populated during sync by KotlinModuleUtils.calculateRunTasks()
at plugins/kotlin/gradle/gradle-java/src/…/KotlinModuleUtils.kt:37.

The critical insight: Amper for JVM reuses the Kotlin/Gradle configuration producers entirely — the isApplicableFor() check on the ExternalSystemExecutionConsoleManager is what routes between Gradle and Amper console handling.
