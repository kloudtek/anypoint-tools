# Deploy CH

POST https://anypoint.mulesoft.com/cloudhub/api/v2/applications

```
------WebKitFormBoundaryBfPgaigCBw94MnZ2
Content-Disposition: form-data; name="appInfoJson"

{"fileName":"banner-sys-api-1.0.0-SNAPSHOT-mule-application.jar","muleVersion":{"version":"4.1.3","updateId":"5b91942decc8fb113f47a3c4"},"properties":{"pkey":"pvalue"},"logLevels":[],"trackingSettings":{"trackingLevel":"METADATA_AND_REPLAY"},"region":"us-east-2","monitoringEnabled":true,"monitoringAutoRestart":true,"persistentQueues":true,"persistentQueuesEncrypted":true,"workers":{"amount":1,"type":{"name":"Micro","weight":0.1,"cpu":"0.1 vCores","memory":"500 MB memory"}},"objectStoreV1":false,"loggingNgEnabled":true,"loggingCustomLog4JEnabled":true,"staticIPsEnabled":true,"domain":"test-deleteme"}
------WebKitFormBoundaryBfPgaigCBw94MnZ2
Content-Disposition: form-data; name="autoStart"

true
------WebKitFormBoundaryBfPgaigCBw94MnZ2
Content-Disposition: form-data; name="file"; filename="banner-sys-api-1.0.0-SNAPSHOT-mule-application.jar"
Content-Type: application/java-archive


------WebKitFormBoundaryBfPgaigCBw94MnZ2--
```

deploy json=

```json
{
  "fileName": "banner-sys-api-1.0.0-SNAPSHOT-mule-application.jar",
  "muleVersion": {
    "version": "4.1.3",
    "updateId": "5b91942decc8fb113f47a3c4"
  },
  "properties": {
    "pkey": "pvalue"
  },
  "logLevels": [],
  "trackingSettings": {
    "trackingLevel": "METADATA_AND_REPLAY"
  },
  "region": "us-east-2",
  "monitoringEnabled": true,
  "monitoringAutoRestart": true,
  "persistentQueues": true,
  "persistentQueuesEncrypted": true,
  "workers": {
    "amount": 1,
    "type": {
      "name": "Micro",
      "weight": 0.1,
      "cpu": "0.1 vCores",
      "memory": "500 MB memory"
    }
  },
  "objectStoreV1": false,
  "loggingNgEnabled": true,
  "loggingCustomLog4JEnabled": true,
  "staticIPsEnabled": true,
  "domain": "test-deleteme"
}
```