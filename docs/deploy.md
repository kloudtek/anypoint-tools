from file:

```
------WebKitFormBoundary04GeK2UHWRfgJznY
Content-Disposition: form-data; name="appInfoJson"

{
  "fileName": "anypointtoolsdemo-1.0.0-SNAPSHOT-mule-application.jar",
  "muleVersion": {
    "version": "4.1.5",
    "updateId": "5c18619275bd7114c9ef3b1e"
  },
  "properties": {
    "dsaads": "sdfaswedfasd"
  },
  "logLevels": [],
  "trackingSettings": {
    "trackingLevel": "DISABLED"
  },
  "region": "us-east-2",
  "deploymentGroup": null,
  "monitoringEnabled": true,
  "monitoringAutoRestart": true,
  "persistentQueues": false,
  "persistentQueuesEncrypted": false,
  "workers": {
    "amount": 1,
    "type": {
      "name": "Micro",
      "weight": 0.1,
      "cpu": "0.1 vCores",
      "memory": "500 MB memory"
    }
  },
  "objectStoreV1": true,
  "loggingNgEnabled": true,
  "loggingCustomLog4JEnabled": false,
  "staticIPsEnabled": false,
  "domain": "anypdemoapp"
}
------WebKitFormBoundary04GeK2UHWRfgJznY
Content-Disposition: form-data; name="autoStart"

true
------WebKitFormBoundary04GeK2UHWRfgJznY
Content-Disposition: form-data; name="file"; filename="anypointtoolsdemo-1.0.0-SNAPSHOT-mule-application.jar"
Content-Type: application/java-archive


------WebKitFormBoundary04GeK2UHWRfgJznY--
```

from exchange:

```json
{
  "applicationInfo": {
    "fileName": "customresdeleteme",
    "muleVersion": {
      "version": "4.1.5",
      "updateId": "5c18619275bd7114c9ef3b1e"
    },
    "properties": {},
    "logLevels": [],
    "trackingSettings": {
      "trackingLevel": "DISABLED"
    },
    "region": "us-west-2",
    "deploymentGroup": null,
    "monitoringEnabled": true,
    "monitoringAutoRestart": true,
    "persistentQueues": false,
    "persistentQueuesEncrypted": false,
    "workers": {
      "amount": 1,
      "type": {
        "name": "Micro",
        "weight": 0.1,
        "cpu": "0.1 vCores",
        "memory": "500 MB memory"
      }
    },
    "objectStoreV1": true,
    "loggingNgEnabled": true,
    "loggingCustomLog4JEnabled": false,
    "staticIPsEnabled": false,
    "domain": "zzzzdeletemeanothertest"
  },
  "applicationSource": {
    "source": "EXCHANGE",
    "groupId": "e31ffbce-99c8-49a7-be77-61cccdab240b",
    "artifactId": "customresdeleteme",
    "version": "122.0.0",
    "organizationId": "e31ffbce-99c8-49a7-be77-61cccdab240b"
  },
  "autoStart": true
}
```

# Update from Exchange

```
{
  "applicationInfo": {},
  "applicationSource": {
    "groupId": "d002eff8-84a5-452f-bce6-145f4a688311",
    "artifactId": "anypointtoolsdemo",
    "version": "1.0.0-SNAPSHOT-20190101203411",
    "organizationId": "d002eff8-84a5-452f-bce6-145f4a688311",
    "source": "EXCHANGE"
  }
}
```

# Updating app metadata

```
------WebKitFormBoundaryXT8wNgZ6G1ho02Cd
Content-Disposition: form-data; name="appInfoJson"
{
  "monitoringEnabled": true,
  "monitoringAutoRestart": true,
  "persistentQueues": false,
  "persistentQueuesEncrypted": false,
  "workers": {
    "type": {
      "name": "Micro",
      "weight": 0.1,
      "cpu": "0.1 vCores",
      "memory": "500 MB memory"
    },
    "amount": 1,
    "remainingOrgWorkers": 1.8,
    "totalOrgWorkers": 1.9
  },
  "trackingSettings": {
    "trackingLevel": "DISABLED"
  },
  "properties": {},
  "logLevels": [],
  "fileName": "anypointtoolsdemo.jar",
  "lastUpdateTime": 1546404360051,
  "propertiesOptions": {},
  "secureDataGatewayEnabled": false,
  "staticIPsEnabled": false,
  "ipAddresses": [],
  "loggingNgEnabled": true,
  "loggingCustomLog4JEnabled": false,
  "status": "UNDEPLOYED",
  "region": "us-east-2",
  "objectStoreV1": false,
  "muleVersion": {
    "version": "4.1.5",
    "updateId": "5c18619275bd7114c9ef3b1e"
  }
}
------WebKitFormBoundaryXT8wNgZ6G1ho02Cd--
```