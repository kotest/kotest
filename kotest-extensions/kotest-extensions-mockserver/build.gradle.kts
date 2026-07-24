plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.mockserver.netty)
            api(libs.mockserver.client.java)

            // mockserver pulls commons-beanutils 1.9.4 transitively (via json-path), which is
            // affected by CVE-2025-48734. Pull 1.11.0 explicitly to override it: it fixes the
            // vulnerability and is still Java 8+ compatible. (The fixed version otherwise only
            // arrives via json-path 3.0.0, which requires Java 17 and would break Kotest's Java 11
            // baseline.) See #6139.
            api(libs.apache.commons.beanutils)
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.fuel)
            implementation(libs.mockk)
         }
      }
   }
}
