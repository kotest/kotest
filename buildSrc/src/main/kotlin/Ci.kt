object Ci {

   private val isGithub = System.getenv("GITHUB_ACTIONS") == "true"
   private val githubBuildNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
   val isReleaseVersion = !isGithub

   val ideaActive = System.getProperty("idea.active") == "true"
   val os = org.gradle.internal.os.OperatingSystem.current()

   private val snapshotBuildNumber = lazy {
      Runtime.getRuntime().exec("git rev-list --count master")
      val number = System.`in`.bufferedReader().read()
      println("Snapshot build number: $number")
      number
   }

   private const val releaseVersion = "4.1.0.RC1"
   private val snapshotVersion = lazy { "4.1.0.${githubBuildNumber}-SNAPSHOT" }
   val publishVersion = lazy { if (isReleaseVersion) releaseVersion else snapshotVersion.value }
}
