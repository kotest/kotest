object Travis {
   val isTravis = false // System.getenv("TRAVIS").equals("true")
   val travisBuildNumber = "0" // System.getenv("TRAVIS_BUILD_NUMBER") ?: "0"
   val isReleaseVersion = false // !isTravis
}
