/*
 *  Copyright 2020 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.curity.identityserver.plugin.RedirectAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticatedSessions;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult;
import se.curity.identityserver.sdk.errors.ErrorCode;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.WebServiceClient;
import se.curity.identityserver.sdk.service.WebServiceClientFactory;
import se.curity.identityserver.sdk.service.authenticationaction.AuthenticatorDescriptor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static se.curity.identityserver.sdk.authenticationaction.completions.RequiredActionCompletion.Redirect.redirect;

public final class RedirectActionAuthenticationAction implements AuthenticationAction
{
    private final Logger _logger = LoggerFactory.getLogger(RedirectActionAuthenticationAction.class);
    private final WebServiceClient _client;
    private final Json _jsonService;
    private final SessionManager _sessionManager;
    private final String _redirectURI;

    public RedirectActionAuthenticationAction(RedirectActionAuthenticationActionConfig configuration) throws URISyntaxException
    {
        _client = getWebServiceClient(configuration, configuration.getRedirectURL() + configuration.getResultPath());
        _jsonService = configuration.getJsonService();
        _sessionManager = configuration.getSessionManager();
        _redirectURI = configuration.getRedirectURL() + configuration.getRedirectPath() + "?status=";
    }

    @Override
    public AuthenticationActionResult apply(AuthenticationAttributes authenticationAttributes,
                                            AuthenticatedSessions authenticatedSessions,
                                            String authenticationTransactionId,
                                            AuthenticatorDescriptor authenticatorDescriptor)
    {
        String key = authenticationAttributes.getSubject() + authenticatorDescriptor.getId();

        Attribute parameterFromSession = _sessionManager.get(key);

        if (parameterFromSession == null)
        {
            return redirectResponse(key);
        }
        else
        {
            HttpResponse response = getExternalStatus(parameterFromSession);

            if (response.statusCode() != 200)
            {
                return AuthenticationActionResult.failedResult("Couldn't verify external status");
            }

            Map<String, Object> responseJson = response.body(HttpResponse.asJsonObject(_jsonService));

            String externalUserId = (String) responseJson.get("externalUserId");
            String externalStatus = (String) responseJson.get("externalStatus");

            _logger.debug("Adding data to authentication: {}, {}", externalUserId, externalStatus);
            authenticationAttributes.append(Attribute.of("externalUserId", externalUserId));
            authenticationAttributes.append(Attribute.of("externalStatus", externalStatus));

            return AuthenticationActionResult.successfulResult(authenticationAttributes);
        }


    }

    private HttpResponse getExternalStatus(Attribute parameterFromSession)
    {
        return _client.withQuery("status=" + parameterFromSession.getValueOfType(String.class)).request().get().response();
    }

    private AuthenticationActionResult redirectResponse(String key)
    {
        UUID uuid = UUID.randomUUID();
        _sessionManager.put(Attribute.of(key, uuid.toString()));

        try
        {
            URI redirectUri = new URI(_redirectURI + uuid);
            return AuthenticationActionResult.pendingResult(redirect(redirectUri));
        }
        catch (URISyntaxException e)
        {
            _logger.warn("Could not construct URI for URL: {}", _redirectURI + uuid);
            throw new RuntimeException();
        }
    }

    private WebServiceClient getWebServiceClient(RedirectActionAuthenticationActionConfig configuration, String uri)
    {
        WebServiceClientFactory factory = configuration.getWebServiceClientFactory();

        Optional<HttpClient> httpClient = configuration.getHttpClient();
        URI u = URI.create(uri);

        if (httpClient.isPresent())
        {
            HttpClient h = httpClient.get();
            String configuredScheme = h.getScheme();
            String requiredScheme = u.getScheme();

            if (!Objects.equals(configuredScheme, requiredScheme))
            {
                _logger.debug("HTTP client was configured with the scheme {} but {} was expected. Ensure that the " +
                        "configuration is correct.", configuredScheme, requiredScheme);

                throw configuration.getExceptionFactory().internalServerException(ErrorCode.CONFIGURATION_ERROR,
                        String.format("HTTP scheme of client is not acceptable; %s is required but %s was found",
                                requiredScheme, configuredScheme));
            }

            return factory.create(h, u.getPort()).withHost(u.getHost()).withPath(u.getPath()).withQuery(u.getQuery());
        }
        else
        {
            return factory.create(u).withQuery(u.getQuery());
        }
    }
}
