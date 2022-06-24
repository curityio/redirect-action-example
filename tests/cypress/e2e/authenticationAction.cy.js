/*
 * Copyright 2022 Curity AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { registerCurityCommands } from "@curity/cypress-commands"

registerCurityCommands()

describe('Redirect Authentication Action tests', () => {
  it('Verify that the Action properly redirects the user to the external system and sets data in the Subject Attributes bag.',  () => {

      const parameters = {
          baseURL: 'https://localhost:8443/oauth/v2/oauth-authorize',
          clientID: 'oauth-assistant-client',
          redirectURI: 'http://localhost:8080/',
          responseType: 'code id_token',
          scope: 'openid',
          extraParams: {
              nonce: '1234'
          }
      }

    // Start the authorization flow
    cy.startAuthorization(parameters)

    // Enter username and click "Next"
    cy.get('#username').type("test")
    cy.get('button[type=submit]').click()

    // Verify that id_token returned contains the external_user_id claim
    cy.getIDTokenClaims().then(claims => {
        expect(claims.external_user_id).to.exist
        expect(claims.external_status).to.exist.and.equal('ACTIVE')
    })
  })
})
