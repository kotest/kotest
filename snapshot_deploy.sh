#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    GPG_TTY=$(tty)
    export GPG_TTY
  ./gradlew uploadArchives -PossrhUsername=${OSSRH_USERNAME} -PossrhPassword=${OSSRH_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE}
fi
