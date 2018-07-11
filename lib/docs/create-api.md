# Anypoint 2.0

## Request

POST https://anypoint.mulesoft.com/apimanager/api/v1/organizations/afac985c-1a3b-4750-89ab-58589dca0bb8/environments/6507f2de-72c6-499b-b1d3-afc70406ffbc/apis


```
{
  "endpoint": {
    "type": "rest-api",
    "uri": "https://foo",
    "proxyUri": null,
    "isCloudHub": null,
    "deploymentType": "CH",
    "referencesUserDomain": null,
    "responseTimeout": null,
    "muleVersion4OrAbove": true
  },
  "instanceLabel": null,
  "spec": {
    "assetId": "test",
    "version": "1.0.0",
    "groupId": "afac985c-1a3b-4750-89ab-58589dca0bb8"
  }
}
```

## Response


```
{
  "assetVersion": "1.0.0",
  "productVersion": "v1",
  "environmentId": "6507f2de-72c6-499b-b1d3-afc70406ffbc",
  "instanceLabel": null,
  "order": 1,
  "audit": {
    "created": {
      "date": "2018-05-31T05:08:42.290Z"
    },
    "updated": {}
  },
  "masterOrganizationId": "88d26fee-1ab8-4462-beb5-0481da09529e",
  "organizationId": "afac985c-1a3b-4750-89ab-58589dca0bb8",
  "id": 14431355,
  "groupId": "afac985c-1a3b-4750-89ab-58589dca0bb8",
  "assetId": "test",
  "tags": [],
  "endpoint": {
    "type": "raml",
    "uri": "https://foo",
    "proxyUri": null,
    "isCloudHub": null,
    "deploymentType": "CH",
    "referencesUserDomain": null,
    "responseTimeout": null,
    "muleVersion4OrAbove": true,
    "audit": {
      "created": {
        "date": "2018-05-31T05:08:42.290Z"
      },
      "updated": {}
    },
    "masterOrganizationId": "88d26fee-1ab8-4462-beb5-0481da09529e",
    "organizationId": "afac985c-1a3b-4750-89ab-58589dca0bb8",
    "id": 197823,
    "apiId": 14431355
  },
  "autodiscoveryInstanceName": "v1:14431355"
}
```