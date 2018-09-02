#!/bin/bash

command -v jq >/dev/null 2>&1 || { echo >&2 "command 'jq' (https://stedolan.github.io/jq/) not found, aborting"; exit 1; }
command -v curl >/dev/null 2>&1 || { echo >&2 "command 'curl' not found, aborting"; exit 1; }

set -o errexit -o pipefail -o noclobber -o nounset

! getopt --test > /dev/null
if [[ ${PIPESTATUS[0]} -ne 4 ]]; then
    echo "I’m sorry, `getopt --test` failed in this environment."
    exit 1
fi

OPTIONS=dfo:v
LONGOPTS=debug,force,output:,verbose

