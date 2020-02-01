package com.sksamuel.kotest

import io.kotest.Tag

fun isTravis() = System.getenv("TRAVIS") == "true"
fun isAppveyor() = System.getenv("APPVEYOR") == "True"
fun isGitHubActions() = System.getenv("GITHUB_ACTIONS") == "true"
fun isCI() = isTravis() || isAppveyor() || isGitHubActions()

object AppveyorTag : Tag()
object TravisTag : Tag()
object GithubActionsTag : Tag()
