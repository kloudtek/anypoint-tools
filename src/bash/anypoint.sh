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
    JSON=$(curl -s -H "${AUTH}" "https://anypoint.mulesoft.com/accounts/api/me")
    ORG_ID=$(echo ${JSON} | jq -r ".user.memberOfOrganizations[] | select ( .name == \"${1}\" ).id")
        failIfUnset "Failed to retrieve org id ${1}" "${ORG_ID}" "${JSON}"
}

getEnvId() {
    JSON=$(curl -s -H "${AUTH}" "https://anypoint.mulesoft.com/accounts/api/organizations/${1}/environments")
    ENV_ID=$(echo ${JSON} | jq -r ".data[] | select ( .name == \"${2}\" ).id")
    failIfUnset "Failed to retrieve org id ${2} from org id ${1}" "${ENV_ID}" "${JSON}"
}

getApiId() {
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

deleteServer() {
    curl -s -X DELETE -H "${AUTH}" -H "X-ANYPNT-ORG-ID:${1}" -H "X-ANYPNT-ENV-ID:${2}" https://anypoint.mulesoft.com/hybrid/api/v1/servers/${3}
}

createServerGroup() {
    SERVER_GROUP=$(curl -s -X POST -d "{\"name\":\"${3}\",\"serverIds\":[$4]}" -H "Content-Type: application/json" -H "${AUTH}" -H "X-ANYPNT-ORG-ID:${1}" -H "X-ANYPNT-ENV-ID:${2}" https://anypoint.mulesoft.com/hybrid/api/v1/serverGroups | jq .data.id)
}

listServersInGroup() {
    listServers $1 $2
    SERVER_IDS=($(echo ${JSON} | jq ".data[0] | select ( .id == $3 ).details.servers[].id"))
}

deleteServerGroup() {
    curl -s -X DELETE -H "${AUTH}" -H "X-ANYPNT-ORG-ID:${1}" -H "X-ANYPNT-ENV-ID:${2}" https://anypoint.mulesoft.com/hybrid/api/v1/serverGroups/${3}
    echo deleted server $3 from org $1 env $2
}

listApps() {
    curl -s -H "${AUTH}" -H "Content-Type: application/json" -H "X-ANYPNT-ORG-ID:${1}" -H "X-ANYPNT-ENV-ID:${2}" https://anypoint.mulesoft.com/hybrid/api/v1/applications?targetId=${3} | jq .
}

authenticate
getOrgId "Integration Group"
getEnvId ${ORG_ID} "tst"
getServerId ${ORG_ID} ${ENV_ID} "tstGroup"
echo ${SERVER_ID}
listApps ${ORG_ID} ${ENV_ID} ${SERVER_ID}

# create server group
#getServerId ${ORG_ID} ${ENV_ID} "tst"
#createServerGroup ${ORG_ID} ${ENV_ID} "testgroup" ${SERVER_ID}

# delete server group
#getServerId ${ORG_ID} ${ENV_ID} "testgroup"
#listServersInGroup ${ORG_ID} ${ENV_ID} ${SERVER_ID}
#deleteServerGroup  ${ORG_ID} ${ENV_ID} ${SERVER_ID}


#getServerRegistrationKey ${ORG_ID} ${ENV_ID}
#echo "Registration key=${ARM_REG_KEY}"
#createEnv ${ORG_ID} "delmenow" "sandbox"
#echo "Created env ${ENV_ID}"
#getApiId "accessmanagement-wcs-exp-api" "v1"

#echo "API ID=${API_ID}"