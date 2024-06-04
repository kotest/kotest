plugins {
   `java-library`
   id("kotest-jvm-conventions")
   application
   alias(libs.plugins.shadowjar)
}

kotlin {
   targets {
      jvm()
   }
}

application {
   mainClass.set("io.kotest.engine.launcher.MainKt")
}

tasks {
   jar {
      archiveClassifier.set("default")
   }
   shadowJar {
      archiveClassifier.set(null as String?)
      archiveBaseName.set("kotest-framework-standalone-jvm")
      exclude("**/module-info.class")
      mergeServiceFiles()
      manifest {
         attributes(Pair("Main-Class", "io.kotest.engine.launcher.MainKt"))
      }
   }
   getByName("jvmJar") {
      finalizedBy(shadowJar)
   }

   startScripts {
      dependsOn(shadowJar)
   }
}

dependencies {
   implementation(kotlin("reflect"))
   implementation(projects.kotestFramework.kotestFrameworkEngine)
}
