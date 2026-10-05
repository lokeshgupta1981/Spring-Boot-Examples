#!/usr/bin/env bash
# Usage: ./loadtest.sh <url> <concurrent-users> <requests>
# Needs 'hey' (https://github.com/rakyll/hey): go install github.com/rakyll/hey@latest
URL=$1
USERS=$2
REQUESTS=$3

# Warm-up run: lets the JIT compile the hot code and opens the HTTP connections
hey -n 2000 -c 200 "$URL" > /dev/null

hey -n "$REQUESTS" -c "$USERS" "$URL"
