import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.tag

object Ci {

   val isGithub = System.getenv("GITHUB_ACTIONS") == "true"
   val githubBuildNumber: String = System.getenv("GITHUB_RUN_ID") ?: "0"

   val isReleaseVersion = !isGithub

   val ideaActive = System.getProperty("idea.active") == "true"
   val os = org.gradle.internal.os.OperatingSystem.current()

   private val snapshotBuildNumber = lazy {
      Runtime.getRuntime().exec("git rev-list --count master")
      System.`in`.bufferedReader().read().toInt()
   }

   private const val releaseVersion = "3.4.2"
   private val snapshotVersion = lazy { "4.0.0.${snapshotBuildNumber.value}-SNAPSHOT" }
   val publishVersion = lazy { if (isGithub) snapshotVersion.value else releaseVersion }
}
