@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
}

kotlin {

   jvm {
      withJava()
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
         }
      }
   }
}

// Normal test task runs on compile JDK.
listOf(8, 11, 17).forEach { ltsVersion ->
   val jdkTest = tasks.register<Test>("jvmTestWithJdk$ltsVersion") {
      javaLauncher.set(javaToolchains.launcherFor {
         languageVersion.set(JavaLanguageVersion.of(ltsVersion))
      })

      description = "Runs the JVM test suite on JDK $ltsVersion"
      group = LifecycleBasePlugin.VERIFICATION_GROUP

      // Copy inputs from normal Test task.
      val testTask = tasks.named("jvmTest", Test::class.java).get()
      classpath = testTask.classpath
      testClassesDirs = testTask.testClassesDirs
   }

   tasks.named("jvmTest").configure { dependsOn(jdkTest) }
}
