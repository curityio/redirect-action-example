#!/bin/bash

##################################################################
# A utility script to run the test setup before running 'npm test'
# First install the act and jq tools, eg by brew install act
##################################################################

LICENSE_FILE_PATH=~/.curity/license.json
GITHUB_PERSONAL_ACCESS_TOKEN=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

cd "$(dirname "${BASH_SOURCE[0]}")"

#
# Prepare the secrets file
#
LICENSE_JWT=$(cat "$LICENSE_FILE_PATH" | jq -r .License)
echo "idsvr_license=$LICENSE_JWT" > ./.secrets

#
# Clone the GitHub actions utility repo
#
rm -rf utils
git clone https://github.com/curityio/github-actions-utilities utils

#
# Build the Docker container in which Cypress tests will be run
#
cd utils/act
docker build -t act-ubuntu-for-cypress .
cd ../../..

#
# Run the test workflow in the Docker container
#
echo "Here"
act -P ubuntu-latest=act-ubuntu-for-cypress -b -s GITHUB_TOKEN="$GITHUB_PERSONAL_ACCESS_TOKEN" workflow_dispatch
