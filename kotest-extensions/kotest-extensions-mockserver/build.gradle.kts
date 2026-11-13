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

            // mockserver 6.x pulls commons-beanutils 1.9.4 and 1.8.3 transitively (via velocity-tools-generic),
            // which are affected by CVE-2025-48734. Pull 1.11.0 explicitly to override it: it fixes the
            // vulnerability and is still Java 8+ compatible. (mockserver only depends on the fixed version
            // from 7.x, which requires Java 17 and would break Kotest's Java 11 baseline.) See #6139.
            api(libs.apache.commons.beanutils)

            // mockserver 6.x pulls netty, jackson, bouncycastle and asm versions with known CVEs, and the
            // releases that fix them (mockserver 7+) need Java 17. Force patched versions instead. See #6258.
            api(project.dependencies.platform(libs.mockserver.patch.netty.bom))
            api(project.dependencies.platform(libs.mockserver.patch.jackson.bom))
            api(project.dependencies.platform(libs.mockserver.patch.asm.bom))
            api(libs.mockserver.patch.bcprov)
            api(libs.mockserver.patch.bcpkix)
            api(libs.mockserver.patch.bcutil)
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
