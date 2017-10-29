# Anypoint CLI

Using the anypoint command line (cli) tool, you will be able to perform various operations against the mulesoft anypoint platform.

## Configuration

Frequently used configuration details (like anypoint username/password, default organization/env) can be saved in a config
file (defaults to ~/.anypoint.cfg).

The configuration files contains various profiles, and specifies which is the default profile it should it:

```json
{
  "profiles" : {
    "default" : {
      "username" : "myusername",
      "password" : "mypassword123",
      "defaultOrganization": "My Org"
      "defaultEnv": "dev"
    },
    "production" : {
      "username" : "produsername",
      "password" : "momo",
      "defaultOrganization": "My Org",
      "defaultEnv": "prod"
    }
  },
  "defaultProfileName" : "default"
}
```

The configuration can be edited by running the command:

```anypoint config``` 

## Get Server Registration Key

In order to retrieve a server registration key, use the command:

```anypoint getregkey```

or for example if you haven't specified a default organization and environment:

```anypoint getregkey -o "My Organization" -e "dev""```
