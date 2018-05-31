# Anypoint Tools

Using the anypoint tool, you will to easily perform complex operations on the mulesoft anypoint platform,
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

Anypoint tools can be used either as a [command line](cli), or as a [maven plugin](maven-plugin)

# Anypoint Legacy Support

# Pre Design Center ([november 2017 release](https://blogs.mulesoft.com/dev/news-dev/anypoint-platform-nov-2017-release/))

If you are still using the pre-november 2017 anypoint platform release (before Design Center), you should stick 
to the version 0.9.27 of anypoint tools, as all versions after that are designed to support the new rest APIs

# License

Anypoint cli is licensed under the CPAL License: https://opensource.org/licenses/CPAL-1.0

# Issues

Report an issues here: https://github.com/Kloudtek/anypoint-tools/issues


