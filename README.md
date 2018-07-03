# Anypoint Tools

Although anypoint brings great capabilities to mule, it also has some severe shortcomings in terms of maintainability and 
scalability.

Anypoint-tools was created to solve those issues by allowing to fully automate API management tasks.

With anypoint tools you can just add a descriptor file to your API project, and when you deploy the application it will
automatically perform all API management setup.

For example with this JSON, it would automatically create the API and a corresponding client application, apply a header injection policy and request access to
the api "otherapi". The client id and secret will be injected in a property or yaml file or your choosing, as well as 
the api id.

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

In addition to provisioning, the anypoint tools deploy capabilities provide important extra features compared to the 
standard deploy:

- It will check that the deployed application has successfully started
- It can perform transformations of deployed applications as part of the deployment
- It can handle anypoint idiosyncrasies/problems (like for example the fact that filtering client apps doesn't always works...).

Anypoint tools can be used either as a [command line](cli), or as a [maven plugin](maven-plugin)

# Anypoint Legacy Support

## Pre Design Center ([november 2017 release](https://blogs.mulesoft.com/dev/news-dev/anypoint-platform-nov-2017-release/))

If you are still using the pre-november 2017 anypoint platform release (before Design Center), you should stick 
to the version 0.9.27 of anypoint tools, as all versions after that are designed to support the new rest APIs

# License

Anypoint tools is licensed under the CPAL License: https://opensource.org/licenses/CPAL-1.0

# Issues

Report an issues here: https://github.com/Kloudtek/anypoint-tools/issues

