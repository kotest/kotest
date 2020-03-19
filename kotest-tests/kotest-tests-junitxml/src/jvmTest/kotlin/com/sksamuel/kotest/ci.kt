package com.sksamuel.kotest

import io.kotest.core.Tag

fun isGitHubActions() = System.getenv("GITHUB_ACTIONS") == "true"
fun isCI() = isGitHubActions()

object GithubActionsTag : Tag()
