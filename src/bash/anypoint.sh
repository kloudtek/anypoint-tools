#!/bin/bash

set -e

if [ -e ~/.anypointlib.cfg ]; then
    CFG=$(cat ~/.anypointlib.cfg)
    USERNAME=$(echo ${CFG} | jq -r .default.username)
    PASSWORD=$(echo ${CFG} | jq -r .default.password)
    ORG_ID=$(echo ${CFG} | jq -r .default.org)
fi

authenticate() {
    if [ "X${AUTH}" == "X" ]; then
        JSON=$(curl -s https://anypoint.mulesoft.com/accounts/login -X POST -H "Content-Type: application/json" --data "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}")
        TOKEN_VALUE=$(echo ${JSON} | jq -r .access_token)
        TOKEN_TYPE=$(echo ${JSON} | jq -r .token_type)
        echo authenticated with token type: ${TOKEN_TYPE}
        AUTH="Authorization: ${TOKEN_TYPE} ${TOKEN_VALUE}"
    fi
}

getApiId() {
    JSON=$(curl -s -H "${AUTH}" "https://anypoint.mulesoft.com/apiplatform/repository/v2/organizations/${ORG_ID}/apis?ascending=false&limit=200&query=${API_NAME}")
    API_ID=$(echo ${JSON} | jq ".apis[] | select(.name == \"${API_NAME}\").versions[] | select(.name == \"${API_VERSION}\").id")
}

authenticate

API_NAME=accessmanagement-wcs-exp-api
API_VERSION=v1

getApiId

echo "API ID=${API_ID}"