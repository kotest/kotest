@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   `java-gradle-plugin`
   alias(libs.plugins.gradle.plugin.publish)
}

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
