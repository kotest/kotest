# Migration Notes

All scanner hits for this repo were reviewed against the receiver-type ladder and
`migration-data.json`. None require a Provider-API rewrite in task 06: every hit is either a
false positive (receiver is a non-Gradle type) or a Kotlin DSL operator/assignment form that
survives the migration unchanged. Details per site below.

## Residual scanner hits — reviewed, no rewrite needed

### `buildSrc/src/main/kotlin/kotlin-conventions.gradle.kts` (Cat-C)
- line 19: `jvmArgumentProviders += SystemPropertiesArgumentProvider(kotestSystemProps)` —
  receiver is a Gradle `Test` task (`JavaForkOptions.jvmArgumentProviders`, kind `list`), but the
  file is `.gradle.kts` where `+=` on a list property is backed by the auto-imported
  `org.gradle.kotlin.dsl.plusAssign` extension. The operator/assignment-overload rule requires
  leaving this form unchanged.

### `kotest-tests/kotest-tests-android-instrumentation/build.gradle.kts` (Cat-C)
- line 23: `excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE*.md}"` — receiver is the Android Gradle
  Plugin packaging DSL (`com.android.build.api.dsl.Packaging.Resources.excludes`, a
  `MutableCollection<String>`), inside `android { packaging { resources { } } }`. Not Gradle's
  `org.gradle.testing.jacoco.plugins.JacocoTaskExtension.excludes`; third-party plugin type out of
  scope.

### `kotest-runner/kotest-runner-junit-platform/src/jvmMain/kotlin/io/kotest/runner/junit/platform/KotestJunitPlatformTestEngine.kt` (Cat-B)
- line 41: `override fun getId(): String = ENGINE_ID` — interface-method override on
  `org.junit.platform.engine.TestEngine`, not `org.gradle.plugin.devel.PluginDeclaration.id`.
- line 42: `override fun getGroupId(): Optional<String> = Optional.of(GROUP_ID)` — interface-method
  override on `org.junit.platform.engine.TestEngine`, not
  `org.gradle.api.publish.maven.MavenPublication.groupId`.

### `kotest-framework/kotest-framework-plugin-gradle/src/main/kotlin/io/kotest/framework/gradle/KotestPlugin.kt` (Cat-D)
- line 286: `classpath = sourceSet.runtimeClasspath` — assignment to a Gradle `JavaExec` task's
  `classpath` (`ConfigurableFileCollection`) in a plain `.kt` file. The `=` form survives via the
  `org.gradle.kotlin.dsl.assign` overload; detect-only, never auto-rewritten. If the file fails to
  compile because the overload is not in scope, the task-07 fix is to add
  `import org.gradle.kotlin.dsl.*`, not to rewrite to `.setFrom(...)`.
- line 489: `classpath = runtimeWithTests` — same construct/receiver as line 286; same treatment.
