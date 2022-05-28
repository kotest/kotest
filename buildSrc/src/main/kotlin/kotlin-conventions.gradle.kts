plugins {
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

testlogger {
   showPassed = false
}

tasks.withType<Test>() {
   useJUnitPlatform()

   filter {
      isFailOnNoMatchingTests = false
   }
}

