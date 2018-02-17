# Anypoint CLI

Using the anypoint command line tool, you will to easily perform complex operations on the mulesoft anypoint platform,
without having to deal with many of it's idiosyncrasies/problems (like for example the fact that filtering client apps 
doesn't always works...).

One of the most powerful features it includes is the ability to include an descriptor in a mule API application which
will automatically provision the API manager, which includes:
- Create APIs provided by the application
- Create portal
- Set endpoint
- Set policies
- Create client application, and set the client id/secret in a property file within the project
- Request access to other APIs

## Installation

### MacOS ([homebrew](https://brew.sh/))

If you have [homebrew](https://brew.sh/) installed on your mac, you can install anypoint cli using the command

```bash
brew install kloudtek/kt/anypoint-tools
```

## Configuration

All configurations parameters can be provided through command line parameters, however you can use `anypoint config` to 
save frequently used parameters like anypoint username/password and default org/env. It can also perform various 
transformations to files within the zip before deploying (like adding properties, regex replace, xslt, etc)

## Commands

### Deploy application

```text
Usage: anypoint deploy [-h] [--force] [-ce] [-co] [-sp] [-sw] [-e=<environmentName>]
              [-et=<createEnvironmentType>] [-o=<organizationName>]
              [-s=<envSuffix>] -t=<target> [-ab=<extraAccess>]...
              [-D=<String=String>]... -f=<appArchives> [-f=<appArchives>]...
              -n=<appNames> [-n=<appNames>]... [-tr=<transforms>]...
Deploy Application
  -h, --help                            Display this help message
  -t, --target=<target>                 Target
  -n, --name=<appNames>                 Application names (there must be one per file
                                            specified, that will be used in the same order
                                            (first name = first file)
  -f, --file=<appArchives>              Application files
  --force                               Force deployment even if app is unchanged
  -sw, --skip-wait-started              Don't wait for application start
  -sp, --skip-provisioning              Skip anypoint provisioning
  -s, --envsuffix=<envSuffix>           Environment suffix (will create a variable 'env'
                                            which will be appended to all provisioned API
                                            versions and all client application names)
  -ab, --accessed-by=<extraAccess>      Extra client applications which should be granted
                                            access to all APIs in the application (note
                                            envSuffix will not be automatically applied to
                                            those)
  -tr, --transform=<transforms>         Json configuration for a package transformer (not implemented yet)
  -D=<String=String>                    Provisioning parameters
  -e, --environment=<environmentName>   Environment
  -ce, --create-environment             Create environment if it doesn't exist (type will
                                        be set to sandbox)
  -et, --env-type=<createEnvironmentType> Environment type if creation required
  -o, --organizationName=<organizationName> Organization
  -co, --create-organizationName        Create organizationName if it doesn't exist
```

This command is used to deploy an anypoint zip file to a server. If the file contains anypoint.json in it's root (add it 
to `src/main/app` in your project) and the `-sp` flag is *NOT* set, then it will update anypoint with all data contained within.

The following example will:
- Create an API called 'deleteme', with a version 'v1'
- Set client-id-enforcement policy
- Create client application 'deleteme', and write it's client id and secret in the property file `classes/cfg.properties` 
within the zip file (using 'mule.client.id' and 'mule.client.secret' as keys)
- Request access to the API `testapp` version `v1`

```json
{
  "apis": [
    {
      "name": "deleteme",
      "version": "v1",
      "endpoint": "${url}/xxx",
      "access": [
        {
          "name": "testapp",
          "version": "v1"
        }
      ],
      "policies": [
        {
          "type": "client-id-enforcement",
          "clientIdExpr": "#[message.inboundProperties[\"client_id\"]]",
          "clientSecretExpr": "#[message.inboundProperties[\"client_secret\"]]"
        }
      ],
      "addCredsToPropertyFile": "classes/cfg.properties"
    }
  ]
}
```

## Get Server Registration Key

```text
Usage: anypoint getregkey [-h] [-ce] [-co] [-e=<environmentName>]
                  [-et=<createEnvironmentType>] [-o=<organizationName>]
 Retrieve a server registration key (requires organization and environment to be
 specified)
   -h, --help                               Display this help message
   -e, --environment=<environmentName>      Environment
   -ce, --create-environment                Create environment if it doesn't exist (type will
                                                be set to sandbox)
   -et, --env-type=<createEnvironmentType>  Environment type if creation required
                                                Default: SANDBOX
   -o, --organizationName=<organizationName> Organization
   -co, --create-organizationName           Create organizationName if it doesn't exist
```

Use this command to retrieve a server registration key from anypoint

## Request API access

```text
Usage: anypoint reqapiaccess [-hr] [-ap] [-co] -a=<apiName> -c=<clientApplicationName>
                    [-o=<organizationName>] [-s=<slaTier>] -v=<apiVersionName>
Request API Access
  -h, --help                                        Display this help message
  -c, --client-application=<clientApplicationName>  Name of the client application
  -a, --api=<apiName>                               Name of the API to request access from
  -v, --apiversion=<apiVersionName>                 Version of the API
  -ap, --approve                                    If flag set it will not automatically approve if
                                                        required
  -r, --restore                                     If flag is set, it will not automatically restore
                                                        access if revoked
  -s, --slatier=<slaTier>                           SLA Tier (required if the api version has more than one SLA
                                                        Tiers assigned)
  -o, --organizationName=<organizationName>         Organization
  -co, --create-organizationName                    Create organizationName if it doesn't exist
```

Use this command to request access to an API

## Add server to server group or cluster

```text
Usage: addservertogroup [-h] [-ce] [-co] [-e=<environmentName>]
                        [-et=<createEnvironmentType>] [-o=<organizationName>]
                        <groupName> <serverName>
Add a server to a group
      <groupName>                       Server Group/Cluster name
      <serverName>                      Server name
  -h, --help                            Display this help message
  -e, --environment=<environmentName>   Environment
  -ce, --create-environment             Create environment if it doesn't exist (type will
                                            be set to sandbox)
  -et, --env-type=<createEnvironmentType> Environment type if creation required
  -o, --organizationName=<organizationName> Organization
  -co, --create-organizationName        Create organizationName if it doesn't exist
```

## Update configuration

```text
Usage: config [-h]
Update configuration
  -h, --help                  Display this help message
```

# License

Anypoint cli is licensed under the CPAL License: https://opensource.org/licenses/CPAL-1.0

# Issues

Report an issues here: https://github.com/Kloudtek/anypoint-tools/issues


