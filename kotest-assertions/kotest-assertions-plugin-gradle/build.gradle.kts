import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   kotlin("jvm")
   `maven-publish`
   `java-gradle-plugin`
   `kotlin-dsl`
   alias(libs.plugins.gradle.plugin.publish)
}

group = "io.kotest"
version = Ci.gradleVersion

repositories {
   mavenCentral()
   mavenLocal()
}

dependencies {
   implementation(libs.kotlin.gradle.plugin)

   testImplementation(projects.kotestAssertions.kotestAssertionsCore)
   testImplementation(projects.kotestFramework.kotestFrameworkApi)
   testImplementation(projects.kotestFramework.kotestFrameworkEngine)
   testImplementation(projects.kotestRunner.kotestRunnerJunit5)

   testImplementation(libs.mockk)
}

gradlePlugin {
   website.set("https://kotest.io")
   vcsUrl.set("https://github.com/kotest")
   plugins {
      create("KotestAssertionsCompilerGradlePlugin") {
         id = "io.kotest.assertions"
         implementationClass = "io.kotest.assertions.plugin.gradle.KotestAssertionsCompilerGradlePlugin"
         displayName = "Kotest Assertions Compiler Plugin"
         description = "Adds support for compiler enhanced assertion failure messages"
         tags.set(listOf("kotest", "kotlin", "testing", "integrationtesting"))
      }
   }
}

kotlin {
   jvmToolchain(17)
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions {
      compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(8)
}

tasks.clean {
   delete("$projectDir/test-project/build/")
   delete("$projectDir/test-project/.gradle/")
}
