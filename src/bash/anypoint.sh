#!/bin/bash

set -e

if [ -e ~/.anypointlib.cfg ]; then
    CFG=$(cat ~/.anypointlib.cfg)
    USERNAME=$(echo ${CFG} | jq -r .default.username)
    PASSWORD=$(echo ${CFG} | jq -r .default.password)
fi

failIfUnset() {
    if [ "$2" == "" ]; then
        echo "$1"
        echo "$3"
        exit -1
    fi
}

authenticate() {
    if [ "X${AUTH}" == "X" ]; then
        JSON=$(curl -s https://anypoint.mulesoft.com/accounts/login -X POST -H "Content-Type: application/json" --data "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}")
        TOKEN_VALUE=$(echo ${JSON} | jq -r .access_token)
        TOKEN_TYPE=$(echo ${JSON} | jq -r .token_type)
        failIfUnset "Failed to authenticate" "${TOKEN_VALUE}" "${JSON}"
        failIfUnset "Failed to authenticate" "${TOKEN_TYPE}" "${JSON}"
        AUTH="Authorization: ${TOKEN_TYPE} ${TOKEN_VALUE}"
        echo authenticated with token type: ${TOKEN_TYPE}
    fi
}

getOrgId() {
    echo "Retrieving id of organization ${1}"
    JSON=$(curl -s -H "${AUTH}" "https://anypoint.mulesoft.com/accounts/api/me")
    ORG_ID=$(echo ${JSON} | jq -r ".user.memberOfOrganizations[] | select ( .name == \"${1}\" ).id")
        failIfUnset "Failed to retrieve org id ${1}" "${ORG_ID}" "${JSON}"
}

getEnvId() {
    echo "Retrieving id of environment ${2} from org id ${1}"
    JSON=$(curl -s -H "${AUTH}" "https://anypoint.mulesoft.com/accounts/api/organizations/${1}/environments")
    ENV_ID=$(echo ${JSON} | jq -r ".data[] | select ( .name == \"${2}\" ).id")
    failIfUnset "Failed to retrieve org id ${2} from org id ${1}" "${ENV_ID}" "${JSON}"
}

getApiId() {
    echo "Retrieving API id"
    JSON=$(curl -s -H "${AUTH}" "https://anypoint.mulesoft.com/apiplatform/repository/v2/organizations/${ORG_ID}/apis?ascending=false&limit=200&query=${1}")
    API_ID=$(echo ${JSON} | jq ".apis[] | select(.name == \"${1}\").versions[] | select(.name == \"${2}\").id")
}

getServerRegistrationKey() {
    ARM_REG_KEY=$(curl -s -H "${AUTH}" -H "X-ANYPNT-ORG-ID:${1}" -H "X-ANYPNT-ENV-ID:${2}" "https://anypoint.mulesoft.com/hybrid/api/v1/servers/registrationToken" | jq -r .data)
}

createEnv() {
    ENV_ID=$(curl -s -H "${AUTH}" -H "Content-Type: application/json" -X POST -d "{\"name\":\"${2}\",\"organizationId\":\"${1}\",\"type\":\"${3}\"}" https://anypoint.mulesoft.com/accounts/api/organizations/${1}/environments | jq -r .id )
}

listServers() {
    JSON=$(curl -s -H "${AUTH}" -H "X-ANYPNT-ORG-ID:${1}" -H "X-ANYPNT-ENV-ID:${2}" https://anypoint.mulesoft.com/armui/api/v1/servers)
}

getServerId() {
    listServers $1 $2
    SERVER_ID=$(echo ${JSON} | jq ".data[0] | select ( .name == \"$3\" ).id ")
}

createServerGroup() {
}

authenticate
getOrgId "Integration Group"
getEnvId ${ORG_ID} "Dev"
getServerId ${ORG_ID} ${ENV_ID} "DevGroup-1"
echo "Server Id = ${SERVER_ID}"
#getServerRegistrationKey ${ORG_ID} ${ENV_ID}
#echo "Registration key=${ARM_REG_KEY}"
#createEnv ${ORG_ID} "delmenow" "sandbox"
#echo "Created env ${ENV_ID}"
#getApiId "accessmanagement-wcs-exp-api" "v1"

#echo "API ID=${API_ID}"