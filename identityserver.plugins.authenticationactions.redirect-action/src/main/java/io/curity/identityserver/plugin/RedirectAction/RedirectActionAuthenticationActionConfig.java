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

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.WebServiceClientFactory;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

public interface RedirectActionAuthenticationActionConfig extends Configuration
{
    @Description("Base URL of the system that the action will redirect to")
    String getRedirectURL();

    @Description("Path of the endpoint to which the action should redirect")
    String getRedirectPath();

    @Description("Path of the endpoint were the redirect  result can be verified")
    String getResultPath();

    @Description("Optional http client configured for communication with the system to which the action redirects")
    Optional<HttpClient> getHttpClient();

    SessionManager getSessionManager();
    WebServiceClientFactory getWebServiceClientFactory();
    ExceptionFactory getExceptionFactory();
    Json getJsonService();
}
