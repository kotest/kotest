object Ci {

   private val isGithub = System.getenv("GITHUB_ACTIONS") == "true"
   val isReleaseVersion = !isGithub

   val ideaActive = System.getProperty("idea.active") == "true"
   val os = org.gradle.internal.os.OperatingSystem.current()

   private val snapshotBuildNumber = lazy {
      Runtime.getRuntime().exec("git rev-list --count master")
      val number = System.`in`.bufferedReader().read()
      println("Snapshot build number: $number")
      number
   }

   private const val releaseVersion = "4.0.0-BETA2"
   private val snapshotVersion = lazy { "4.0.0.${snapshotBuildNumber.value}-SNAPSHOT" }
   val publishVersion = lazy { if (isReleaseVersion) releaseVersion else snapshotVersion.value }
}
