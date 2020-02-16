object Ci {

   val isGithub = System.getenv("GITHUB_ACTIONS") == "true"
   val githubBuildNumber: String = System.getenv("BUILD_NUMBER") ?: "0"

   val isReleaseVersion = !isGithub

   val ideaActive = System.getProperty("idea.active") == "true"
   val os = org.gradle.internal.os.OperatingSystem.current()
}
