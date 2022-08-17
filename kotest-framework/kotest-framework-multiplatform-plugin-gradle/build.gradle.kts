import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   kotlin("jvm")
   `maven-publish`
   `java-gradle-plugin`
   alias(libs.plugins.gradle.plugin.publish)
}

group = "io.kotest"
version = Ci.gradleVersion

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
   mavenCentral()
   mavenLocal()
}

dependencies {
   compileOnly(gradleApi())
   compileOnly(libs.kotlin.gradle.plugin)

   testImplementation(projects.kotestAssertions.kotestAssertionsCore)
   testImplementation(projects.kotestFramework.kotestFrameworkApi)
   testImplementation(projects.kotestFramework.kotestFrameworkEngine)
   testImplementation(projects.kotestRunner.kotestRunnerJunit5)
}

tasks.withType<Test> {
   // Build these libraries ahead of time so that the test project doesn't try to build them itself (if it tries to build them while we are as well, this can lead to conflicts)
   setOf(
      projects.kotestAssertions.kotestAssertionsCore,
      projects.kotestFramework.kotestFrameworkDiscovery,
      projects.kotestFramework.kotestFrameworkEngine,
   ).map { project ->
      project.dependencyProject.path
   }.forEach { projectPath ->
      setOf(
         "jvmJar",
         "compileKotlinLinuxX64",
         "compileKotlinMacosX64",
         "compileKotlinMacosArm64",
         "compileKotlinMingwX64",
      ).forEach { task ->
         dependsOn("$projectPath:$task")
      }
   }

   setOf(
      projects.kotestRunner.kotestRunnerJunit5,
      projects.kotestFramework.kotestFrameworkMultiplatformPluginEmbeddableCompiler,
      projects.kotestFramework.kotestFrameworkMultiplatformPluginLegacyNative,
   ).map { project ->
      project.dependencyProject.path
   }.forEach { project ->
      dependsOn("$project:jvmJar")
   }

   dependsOn("jar")
   dependsOn(":kotlinNpmInstall")

   useJUnitPlatform()

   systemProperty("kotestVersion", Ci.publishVersion)

   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

tasks {
   pluginBundle {
      website = "https://kotest.io"
      vcsUrl = "https://github.com/kotest"
      tags = listOf("kotest", "kotlin", "testing", "integrationTesting", "javascript")
   }
   gradlePlugin {
      plugins {
         create("KotestMultiplatformCompilerGradlePlugin") {
            id = "io.kotest.multiplatform"
            implementationClass = "io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin"
            displayName = "Kotest Multiplatform Compiler Plugin"
            description = "Adds support for Javascript and Native tests in Kotest"
         }
      }
   }
}
