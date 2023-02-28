# Redirect Action Example

[![Quality](https://img.shields.io/badge/quality-demo-red)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

This repository is an example of an Authentication Action Plugin for the
Curity Identity Server, which uses the `redirect()` response type. It consists
of two parts: 

- the plugin code is in the `identityserver.plugins.authenticationactions.redirect-action` directory
- `example` directory which contains a simple demo website to which the plugin action redirects. The demo is written
  in Java and Spring.
 
## Running the example

To run the example follow these steps:

- Start the project by running the command `mvn spring-boot:start` from the `example` directory.
- Install the authentication plugin in your instance of the Curity Identity Server. Refer to the
  [plugin's README](identityserver.plugins.authenticationactions.redirect-action/README.rst) to check how to do it.
- When the plugin is installed, go to the admin UI, then **Authentication Profile** -> **Authenticators**.
  Edit an authenticator of your choice.
- If you want to run the example with a local instance of the Spring application you'll need an HTTP client which uses
  the `http` scheme. To add such client click the `Facilities` button in the top right corner of the screen, scroll down
  to **Http clients** and click the `New` button.
- Enter a suitable name for the client and click `Create`.

![Create Http Client](docs/create-http-client.png)

- Select the `http` scheme for the client and close the popup.

![Select type](docs/http-client-type.png)

- Back in the authenticator form, in the action column click the `Add` button in either Login or SSO flow.
- Click `New Action`, enter a suitable name and select `Redirect Action` type.
- Enter the required configuration.

![Select type](docs/action-configuration.png)

Enter the values from the above screen to run this example.

- Commit your changes. The action plugin is now ready.

## Test with OAuth.tools

You can test the action using [OAuth.tools](https://oauth.tools). Have a look at
[this tutorial](https://curity.io/resources/learn/test-using-oauth-tools/) to learn how to configure OAuth.tools with your
instance of the Curity Identity Server. Once you have an environment configured, you can start any OAuth flow to initiate
authentication. You can add a [Debug Action](https://curity.io/resources/videos/debug-action/) to your authentication flow
to check the parameters set by the redirect action.

## Running Tests Locally with Cypress

To run the test suite locally, first ensure that you have an instance of the Curity Identity Server running with the plugin
installed and using the configuration found in `tests/idsvr/config.xml`. Next, run the example app located in `example`
by using the Maven command `mvn spring-boot:start`. Then, install Cypress by using the following commands.

```bash
    cd tests
    npm i
```

You can then open the Cypress app to run tests with `npm run cypress.open` or run the headless version of the tests with
`npm run cypress.run`.

## Running the GitHub Actions Workflow Locally

To run the GitHub Actions workflow locally refer to this [README](https://github.com/curityio/github-actions-utilities).
Note that in order to run the workflow for this plugin you will need to run the Curity Identity Server directly in the
`act` container, as described at the end of the linked README file.

## More Information

Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server.
