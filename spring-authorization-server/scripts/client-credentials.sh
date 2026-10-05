#!/usr/bin/env bash
# Gets a token for recipe-cli (client_credentials) and calls the resource server with it.
# Needs: curl, python3. Start auth-server (9000) and resource-server (8082) first.
set -euo pipefail

RESPONSE=$(curl -s -u recipe-cli:secret \
  -d grant_type=client_credentials -d scope=recipes.read \
  http://localhost:9000/oauth2/token)
echo "$RESPONSE"

TOKEN=$(printf '%s' "$RESPONSE" | python3 -c "import json,sys; print(json.load(sys.stdin)['access_token'])")

# Decode the JWT payload (the second part of the token)
printf '%s' "$TOKEN" | python3 -c "
import base64, json, sys
payload = sys.stdin.read().split('.')[1]
print(json.dumps(json.loads(base64.urlsafe_b64decode(payload + '=' * (-len(payload) % 4))), indent=2))"

curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8082/recipes
echo
