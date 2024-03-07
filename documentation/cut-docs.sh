#!/bin/bash

###
### After running this script, update the current version to the next value in docusaurus.config.js
###

VERSION=$1
CUT_DOCS_DIR=versioned_docs/version-$VERSION

if [ -z "$VERSION" ]; then
   echo "usage: ./cut-docs.sh <version>"
   exit 1
fi

if [[ $(pwd) != *kotest/documentation ]]; then
   echo "Must be used from kotest/documentation dir"
   exit 2
fi

# add version
VERSIONS=$(cat versions.json | jq '["'"$VERSION"'"] + .')
echo $VERSIONS | jq > versions.json

## save versioned docs
cp -r docs $CUT_DOCS_DIR

# save versioned sidebar
SIDEBAR=$(cat sidebars.js) # load contents of the current sidebars
SIDEBAR1=${SIDEBAR#"module.exports = "} # json doesn't like a prefix
SIDEBAR2=${SIDEBAR1%";"} # json doesn't like the ; suffix
echo $SIDEBAR2 | jq > versioned_sidebars/version-$VERSION-sidebars.json

