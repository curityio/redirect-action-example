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

describe('Redirect Authentication Action tests', () => {
  it('Verify that the Action properly redirects the user to the external system and sets data in the Subject Attributes bag.',  () => {

    const authorizationURL = new URL('https://localhost:8443/oauth/v2/oauth-authorize')
    const params = authorizationURL.searchParams

    params.append('client_id', 'oauth-assistant-client')
    params.append('response_type', 'code id_token')
    params.append('redirect_uri', 'http://localhost:8080/')
    params.append('prompt', 'login')
    params.append('scope', 'openid')
    params.append('nonce', '1234')

    // Start the authorization flow
    cy.visit(authorizationURL.toString())

    // Enter username and click "Next"
    cy.get('#username').type("test")
    cy.get('button[type=submit]').click()

    // Verify that id_token returned contains the external_user_id claim
    cy.url()
        .should('contain', '#')
        .should('contain', 'id_token')
        .then(url => {
          const idToken = url.split('#')[1]
          expect(idToken).not.to.be.empty

          const claims = decodeJWT(idToken)
          expect(claims).to.contain("external_user_id")
        })
  })

})

const decodeJWT = (token) => {
  const payload = token.split('.')[1]
  expect(payload).not.to.be.empty

  return decodeURIComponent(Array.prototype.map.call(atob(payload.replace('-', '+').replace('_', '/')), c =>
      '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
  ).join(''))
}
