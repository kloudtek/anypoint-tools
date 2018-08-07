# Anypoint Tools

## Overview

Although anypoint brings great capabilities to mule, it also has some severe shortcomings which make it very painful to use:

* API Management is all done manually on the anypoint website. Although that's fine for a handful of APIs, it is a *complete
nightmare* when reaching a larger scale of APIs (like 50+). And that's not just because of the work of actually doing all the
manual configuration, but much worse the amount of time spent debugging problems caused by any mistakes from the manual configuration.

* Mule Deploy plugin doesn't check if the application has successfully started. This is a major problem in a CI/CD pipeline
where the CI/CD tool says the deployment was successful, when in reality the application failed to even start successfully.

* Anypoint has historically had some severe quirks/bugs. A few examples which may or maybe not still be present include
filtering of client applications failing in certain cases, random errors invoking anypoint management REST APIs, listing 
exchange assets failing to see some assets which can leave an org in a state of being impossible to delete

* Anypoint Command Line Tool is written in javascript and requires npm to install

Because of all those issues (and some others), I've created anypoint-tools which includes both a command line tool as well
a mule maven plugin for deployment.

## Automated API Management Provisioning

The most important capability of anypoint-tools is to automate API management.

In order to do so, you will need to create a JSON file containing the API configuration details. For example:

```json
{
  "name": "My API",
  "version": "1.0.0",
  "endpoint": "${url}/xxx",
  "policies": [
    {
      "groupId": "68ef9520-24e9-4cf2-b2f5-620025690913",
      "assetId": "header-injection",
      "assetVersion": "1.0.0",
      "policyTemplateId": "59",
      "type": "json",
      "data": {
        "inboundHeaders": [
          {
            "key": "foo",
            "value": "bar"
          }
        ]
      }
    }
  ],
  "access": [
    {
      "groupId": "68ef9520-24e9-4cf2-b2f5-620025690944",
      "assetId": "otherapi",
      "assetVersion": "1.0.0",
    }
  ]
}
```

So in this example, it will 

- Create a API named "My API" version "1.0.0"
- Assign to it a header-injection policy
- Create a client application for the API ( in the format of \[api name in lowercase]-\[org name in lowercase]-\[env name in lowercase] )
- Request API access to api "otherapi" for the client application created in previous step

Right now the only way to use API provisioning is through the deployment of an application through the maven plugin (in 
the future I'll make it so that it can be done in isolation).

so in order to use the maven deployment plugin, you should modify your pom.xml and include the following plugin

```$xml
<plugin>
    <groupId>com.kloudtek.anypoint-tools</groupId>
    <artifactId>anypoint-maven-plugin</artifactId>
    <version>[put the latest version of anypoint-tools maven plugin here]</version>
    <executions>
        <execution>
            <id>deploy-arm</id>
            <phase>deploy</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

And then just set the following properties:

- anypoint.username : your anypoint username
- anypoint.password : your anypoint password
- anypoint.org : Name of the anypoint organization to deploy to
- anypoint.env : Name of the anypoint environment to deploy to
- anypoint.target : Name of the anypoint target (server/server group/cluster) to deploy to

You will then need to include your API provisioning JSON in your project under src/main/resources/anypoint.json

When that file is present, the deployment will automatically perform the API provisioning.

After the provisioning is done, it will also add/modify a file "apiconfig.properties" in the application being deployed, and 
include the following properties:

- anypoint.apiId : (mule 4 only) The API Id (which is environment-specific)
- anypoint.client.id : Client id for the Client Application generated for this API
- anypoint.client.secret : Client secret for the Client Application generated for this API

# Anypoint Legacy Support

## Pre Design Center ([november 2017 release](https://blogs.mulesoft.com/dev/news-dev/anypoint-platform-nov-2017-release/))

If you are still using the pre-november 2017 anypoint platform release (before Design Center), you should stick 
to the version 0.9.27 of anypoint tools, as all versions after that are designed to support the new rest APIs

# License

Anypoint tools is licensed under the CPAL License: https://opensource.org/licenses/CPAL-1.0

# Issues

Report an issues here: https://github.com/Kloudtek/anypoint-tools/issues

