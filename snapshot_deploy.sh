#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -K $encrypted_1aa081e50934_key -iv $encrypted_1aa081e50934_iv -in sksamuel.gpg.enc -out sksamuel.gpg -d
  echo ./sksamuel.gpg
  ./gradlew uploadArchives -PossrhUsername=${OSSRH_USERNAME} -PossrhPassword=${OSSRH_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=./sksamuel.gpg
fi
