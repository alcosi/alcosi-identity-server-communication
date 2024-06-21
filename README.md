### Library for low-level communication with Alcosi (C#) identity service with spring boot initialization.

### Package Authentication
There are packages that require authentication, including libraries/plugins developed by us which will go into OpenSource. Even when they are publicly available, a login to GitHub is necessary.
- Example from the build file:

````kotlin
repositories {
//other repositories
    maven {
        name = "GitHub"
        url = uri("https://maven.pkg.github.com/alcosi/alcosi-identity-server-communication")
        credentials {
            username = gitUsername //"${System.getenv()["GIHUB_PACKAGE_USERNAME"]}"
            password = gitToken //"${System.getenv()["GIHUB_PACKAGE_TOKEN"]}"
        }
    }
}
````
    - For local builds, credentials are taken from environment variables. Under CI/CD, your own accounts should be used, and these variables must be substituted accordingly.


### Using:

- To use just add as dependency:

````kotlin
dependencies {
//Other dependencies
    implementation("com.alcosi:alcosi-identity-server-communication:${version}")
//Other dependencies
}
````



- Then get required components from spring boot context

| Package                             | Name                                   |
|-------------------------------------|----------------------------------------|
| `com/alcosi/identity/service/api`   | `IdentityActivationCodeComponent`      |
| `com/alcosi/identity/service/api`   | `IdentityChangeComponent`              |
| `com/alcosi/identity/service/api`   | `IdentityChangeContactComponent`       |
| `com/alcosi/identity/service/api`   | `IdentityClaimsComponent`              |
| `com/alcosi/identity/service/api`   | `IdentityDeleteComponent`              |
| `com/alcosi/identity/service/api`   | `IdentityGetProfileComponent`          |
| `com/alcosi/identity/service/api`   | `IdentityRegistrationComponent`        |
| `com/alcosi/identity/service/api`   | `IdentityResetPasswordComponent`       |
| `com/alcosi/identity/service/api`   | `IdentityRevokeTokenComponent`         |
| `com/alcosi/identity/service/api`   | `IdentityTwoFactorComponent`           |
| `com/alcosi/identity/service/ids`   | `IdentityGetProfileIdByTokenComponent` |
| `com/alcosi/identity/service/ids`   | `IdentityIntrospectTokenComponent`     |
| `com/alcosi/identity/service/ids`   | `IdentityProfileIdByTokenProvider`     |
| `com/alcosi/identity/service/ids`   | `IdentityTokenComponent`               |
| `com/alcosi/identity/service/token` | `IdentityClientTokenHolder`            |

Configure with properties:

| Parameter                                                                  | Type             | Description                                                             |
|----------------------------------------------------------------------------|------------------|-------------------------------------------------------------------------|
| `identity-service.enabled`                                                 | boolean          | Enable autoconfig for this starter                                      |
| `identity-service.connect-timeout`                                         | Duration         | Connect timeout                                                         |
| `identity-service.read-timeout`                                            | Duration         | Read timeout                                                            |
| `identity-service.service-ip`                                              | String           | Ip of this machine to pass as Ip for Machine2Machine token getting      |
| `identity-service.api.uri`                                                 | URI              | URI of Identity Server API                                              |
| `identity-service.api.api-version`                                         | String           | Identity Server Api version                                             |
| `identity-service.ids.uri`                                                 | URI              | URI of Identity Server                                                  |
| `identity-service.ids.ip-header`                                           | String           | Header name to pass ip to           Identity Server                     |
| `identity-service.ids.user-agent-header`                                   | String           | Header name to pass User-Agent to           Identity Server             |
| `identity-service.ids.introspection-client.id`                             | String           | Id of Client to introspect profile tokens                               |
| `identity-service.ids.introspection-client.scope`                          | String           | Scopes of Client to introspect profile tokens  (delimited by space ` `) |
| `identity-service.ids.introspection-client.secret`                         | String           | Password of Client to introspect profile tokens                         |
| `identity-service.ids.api-client.id`                                       | String           | Id of Client Machine2Machine                                            |
| `identity-service.ids.api-client.scope`                                    | String           | Scopes of Client Machine2Machine  (delimited by space ` `)              |
| `identity-service.ids.api-client.secret`                                   | String           | Password of Client Machine2Machine                                      |
| `identity-service.ids.api-client.grant-type`                               | String           | Machine2Machine grant type                                              |
| `identity-service.logging.logging-interceptor.enabled`                     | Boolean          | Enable autoconfig for this starter                                      |
| `identity-service.logging.logging-interceptor.logging-level`               | JavaLoggingLevel | Logging level of messages                                               |
| `identity-service.logging.logging-interceptor.max-body-size`               | Int              | Max logging body size                                                   |
| `identity-service.logging.logging-interceptor.order`                       | Int              | Filter order (Ordered interface)                                        |
| `identity-service.logging.logging-interceptor.new-line-column-symbols`     | Int              | How many symbols in first column (param name)                           |
| `identity-service.logging.logging-interceptor.request.id-included`         | Boolean          | Is request id included to log message (request)                         |
| `identity-service.logging.logging-interceptor.request.uri-included`        | Boolean          | Is uri included to log message (request)                                |
| `identity-service.logging.logging-interceptor.request.took-time-included`  | Boolean          | Is timing included to log message (request)                             |
| `identity-service.logging.logging-interceptor.request.headers-included`    | Boolean          | Is headers included to log message (request)                            |
| `identity-service.logging.logging-interceptor.request.body-included`       | Boolean          | Is body included to log message (request)                               |
| `identity-service.logging.logging-interceptor.response.id-included`        | Boolean          | Is request id included to log message (response)                        |
| `identity-service.logging.logging-interceptor.response.uri-included`       | Boolean          | Is uri included to log message (response)                               |
| `identity-service.logging.logging-interceptor.response.took-time-included` | Boolean          | Is timing included to log message (response)                            |
| `identity-service.logging.logging-interceptor.response.headers-included`   | Boolean          | Is headers included to log message (response)                           |
| `identity-service.logging.logging-interceptor.response.body-included`      | Boolean          | Is body included to log message (response)                              |
