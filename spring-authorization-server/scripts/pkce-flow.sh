#!/usr/bin/env bash
# Runs the authorization_code + PKCE flow for recipe-web with curl only:
# the cookie jar plays the browser, and the login form is posted with its CSRF token.
# Needs: curl, openssl, python3. Start auth-server (9000) and resource-server (8082) first.
set -euo pipefail
JAR=$(mktemp)

VERIFIER=$(openssl rand -base64 48 | tr '+/' '-_' | tr -d '=\n' | cut -c1-64)
CHALLENGE=$(printf '%s' "$VERIFIER" | openssl dgst -sha256 -binary | openssl base64 | tr '+/' '-_' | tr -d '=\n')

AUTHORIZE="http://localhost:9000/oauth2/authorize?response_type=code&client_id=recipe-web"
AUTHORIZE="$AUTHORIZE&redirect_uri=http://127.0.0.1:8080/callback&scope=openid%20recipes.read"
AUTHORIZE="$AUTHORIZE&state=abc123&code_challenge=$CHALLENGE&code_challenge_method=S256"

# 1. Not logged in yet: the server saves the request and redirects to /login
curl -s -o /dev/null -c "$JAR" -b "$JAR" -H "Accept: text/html" "$AUTHORIZE"

# 2. Post the login form with its CSRF token
CSRF=$(curl -s -c "$JAR" -b "$JAR" http://localhost:9000/login \
  | grep -o 'name="_csrf" type="hidden" value="[^"]*"' | sed 's/.*value="//;s/"//')
curl -s -o /dev/null -c "$JAR" -b "$JAR" \
  -d username=lokesh -d password=password -d "_csrf=$CSRF" http://localhost:9000/login

# 3. Logged in: the server redirects to the client's redirect URI with the code
LOCATION=$(curl -s -o /dev/null -w '%{redirect_url}' -c "$JAR" -b "$JAR" "$AUTHORIZE")
echo "$LOCATION"
CODE=$(printf '%s' "$LOCATION" | sed 's/.*code=//;s/&.*//')

# 4. Exchange the code and the verifier for tokens (public client, no secret)
RESPONSE=$(curl -s -d grant_type=authorization_code -d client_id=recipe-web \
  -d "code=$CODE" -d redirect_uri=http://127.0.0.1:8080/callback \
  -d "code_verifier=$VERIFIER" http://localhost:9000/oauth2/token)
echo "$RESPONSE"

TOKEN=$(printf '%s' "$RESPONSE" | python3 -c "import json,sys; print(json.load(sys.stdin)['access_token'])")
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8082/me
echo
rm -f "$JAR"
