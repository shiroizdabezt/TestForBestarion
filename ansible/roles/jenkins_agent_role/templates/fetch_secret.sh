#!/bin/bash
# Usage: ./fetch_jnlp.sh <controller_url> <agent_name> <user> <token> <output_file>

CONTROLLER_URL="$1"
AGENT_NAME="$2"
USER="$3"
TOKEN="$4"
OUTPUT_FILE="$5"

curl -s -u "${USER}:${TOKEN}" \
  "${CONTROLLER_URL}/computer/${AGENT_NAME}/jenkins-agent.jnlp" \
  -o "${OUTPUT_FILE}"

echo "Saved to ${OUTPUT_FILE}"
