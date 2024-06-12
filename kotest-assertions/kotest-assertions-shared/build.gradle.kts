plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
}

kotlin {
   val jdkVersionAttribute = Attribute.of("org.gradle.jvm.version", Int::class.javaObjectType)

   jvm("jdk8") {
      attributes.attribute(jdkVersionAttribute, 8)
      jvmToolchain(8)
   }

   jvm("jdk21") {
      attributes.attribute(jdkVersionAttribute, 21)
      jvmToolchain(21)
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            // this is api because we want to expose `shouldBe` etc
            api(projects.kotestAssertions.kotestAssertionsApi)

            implementation(kotlin("reflect"))
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils)
            implementation(libs.opentest4j)
         }
      }

      val jdk8Main by getting {
         dependsOn(jvmMain)
      }

      val jdk21Main by getting {
         dependsOn(jvmMain)
      }
   }
}
