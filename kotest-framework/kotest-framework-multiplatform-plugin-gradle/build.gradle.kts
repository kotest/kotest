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

   testImplementation(project(Projects.Assertions.Core))
   testImplementation(project(Projects.Framework.api))
   testImplementation(project(Projects.Framework.engine))
   testImplementation(project(Projects.JunitRunner))
}

tasks.withType<Test> {
   // Build these libraries ahead of time so that the test project doesn't try to build them itself (if it tries to build them while we are as well, this can lead to conflicts)
   setOf(
      Projects.Assertions.Core,
      Projects.Framework.api,
      Projects.Framework.engine
   ).forEach { project ->
      setOf(
         "jvmJar",
         "compileKotlinLinuxX64",
         "compileKotlinMacosX64",
         "compileKotlinMacosArm64",
         "compileKotlinMingwX64",
      ).forEach { task ->
         dependsOn("$project:$task")
      }
   }

   setOf(
      Projects.JunitRunner,
      ":kotest-framework:kotest-framework-multiplatform-plugin-js",
      ":kotest-framework:kotest-framework-multiplatform-plugin-native"
   ).forEach { project ->
      dependsOn("$project:jvmJar")
   }

   dependsOn("jar")

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
