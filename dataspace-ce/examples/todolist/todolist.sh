#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'  # No Color

# Determine SED_INPLACE depending on OS
if [[ "$OSTYPE" == "darwin"* ]]; then
  SED_INPLACE=(-i '')
else
  SED_INPLACE=(-i)
fi

# Function to check if the last command was successful
check_success() {
    if [ $? -ne 0 ]; then
        echo -e "${RED}Error: $1${NC}"
        exit 1
    fi
}

# Generate a 6-digit password using $RANDOM
generate_password() {
    printf "%06d" $RANDOM
}

# Function to fetch JWKS
fetch_jwks() {
    local KEYCLOAK_HOST=${KEYCLOAK_HOSTNAME:-localhost}
    local KEYCLOAK_PORT=${KEYCLOAK_PORT:-8180}
    local REALM="todos"
    local JWKS_FILE_PATH="files/jwks.json"
    local JWKS_URL="http://${KEYCLOAK_HOST}:${KEYCLOAK_PORT}/realms/${REALM}/protocol/openid-connect/certs"

    echo -e "${YELLOW}Checking Keycloak availability...${NC}"
    local max_attempts=40
    local attempt=1
    while [ $attempt -le $max_attempts ]; do
        if curl -s -o /dev/null -w "%{http_code}" "http://${KEYCLOAK_HOST}:${KEYCLOAK_PORT}/realms/${REALM}" | grep -q "200"; then
            echo -e "${GREEN}Keycloak is available.${NC}"
            break
        fi
        echo -e "${YELLOW}Attempt $attempt/$max_attempts: Keycloak is not available. Waiting...${NC}"
        sleep 5
        attempt=$((attempt+1))
    done

    if [ $attempt -gt $max_attempts ]; then
        echo -e "${RED}Keycloak did not become available after $max_attempts attempts.${NC}"
        return 1
    fi

    echo -e "${YELLOW}Fetching JWKS from ${JWKS_URL} and saving to ${JWKS_FILE_PATH}...${NC}"

        SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
        cd "$SCRIPT_DIR"

        mkdir -p files

        curl -s http://localhost:8180/realms/todos/protocol/openid-connect/certs > files/jwks.json && cat files/jwks.json

    if [ $? -eq 0 ] && [ -s "files/jwks.json" ]; then
        echo -e "${GREEN}JWKS successfully saved to ${JWKS_FILE_PATH}${NC}"
    else
        echo -e "${RED}Failed to save JWKS to file or file is empty: ${JWKS_FILE_PATH}${NC}"
        return 1
    fi
}


# Step 1: Read configuration from docker/.env
echo -e "${YELLOW}Reading configuration from ../../docker/.env...${NC}"
if [ ! -f ../../docker/.env ]; then
    echo -e "${RED}File ../../docker/.env not found. Please create and configure it manually.${NC}"
    exit 1
fi
source ../../docker/.env

# Generate password if not set
if [ -z "$POSTGRES_PASSWORD" ]; then
    POSTGRES_PASSWORD=$(generate_password)
    if grep -q "^POSTGRES_PASSWORD=" ../../docker/.env; then
        sed "${SED_INPLACE[@]}" "s/^POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=${POSTGRES_PASSWORD}/" ../../docker/.env
    else
        echo "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}" >> ../../docker/.env
    fi
    echo -e "${GREEN}Generated PostgreSQL password: ${POSTGRES_PASSWORD}${NC}"
fi

# Generate Keycloak password if not set
if [ -z "$KEYCLOAK_ADMIN_PASSWORD" ]; then
    KEYCLOAK_ADMIN_PASSWORD=$(generate_password)
    if grep -q "^KEYCLOAK_ADMIN_PASSWORD=" ../../docker/.env; then
        sed "${SED_INPLACE[@]}" "s/^KEYCLOAK_ADMIN_PASSWORD=.*/KEYCLOAK_ADMIN_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD}/" ../../docker/.env
    else
        echo "KEYCLOAK_ADMIN_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD}" >> ../../docker/.env
    fi
    echo -e "${GREEN}Generated Keycloak admin password: ${KEYCLOAK_ADMIN_PASSWORD}${NC}"
fi

# Add Keycloak DB password if not set
if [ -z "$KEYCLOAK_DB_PASSWORD" ]; then
    KEYCLOAK_DB_PASSWORD=$(generate_password)
    if grep -q "^KEYCLOAK_DB_PASSWORD=" ../../docker/.env; then
        sed "${SED_INPLACE[@]}" "s/^KEYCLOAK_DB_PASSWORD=.*/KEYCLOAK_DB_PASSWORD=${KEYCLOAK_DB_PASSWORD}/" ../../docker/.env
    else
        echo "KEYCLOAK_DB_PASSWORD=${KEYCLOAK_DB_PASSWORD}" >> ../../docker/.env
    fi
    echo -e "${GREEN}Generated Keycloak DB password: ${KEYCLOAK_DB_PASSWORD}${NC}"
fi

# Step 2: Ensure the custom network exists for todolist project.
echo -e "${YELLOW}Checking if the custom network 'todolist_network' exists...${NC}"
if ! docker network ls | grep -q "todolist_network"; then
    echo -e "${YELLOW}Creating custom network 'todolist_network'...${NC}"
    docker network create todolist_network || check_success "Failed to create custom network"
else
    echo -e "${GREEN}Custom network 'todolist_network' already exists.${NC}"
fi

# Step 3: Remove conflicting containers if they exist.
CONTAINERS=("postgres-todolist" "dataspace-builder-todolist" "dataspace-app-todolist" "keycloak" "keycloak-db")
for CONTAINER in "${CONTAINERS[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
        echo -e "${YELLOW}Removing existing container '${CONTAINER}'...${NC}"
        docker rm -f ${CONTAINER} || check_success "Failed to remove conflicting container '${CONTAINER}'"
    else
        echo -e "${GREEN}No conflicting container '${CONTAINER}' found.${NC}"
    fi
done

# Step 4: Update context-child.properties with environment values for todolist model.
echo -e "${YELLOW}Updating examples/todolist/files/context-child.properties...${NC}"
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_HOST}|${POSTGRES_HOST}|g" files/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_PORT}|${POSTGRES_PORT}|g" files/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_SCHEMA_NAME}|${POSTGRES_SCHEMA_NAME}|g" files/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_DATABASE}|${POSTGRES_DATABASE}|g" files/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_USERNAME}|${POSTGRES_USERNAME}|g" files/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_PASSWORD}|${POSTGRES_PASSWORD}|g" files/context-child.properties

check_success "Failed to update context-child.properties"

# Step 5: Detect architecture and set platform flag if needed
ARCH=$(uname -m)
if [[ "$ARCH" == "arm64" ]]; then
    echo -e "${YELLOW}Detected ARM architecture${NC}"
else
    echo -e "${YELLOW}Detected x86 architecture${NC}"
fi

# Step 6: Split container startup into three phases

# -------------------------------------------------------
# Phase 1: Launch core containers (postgres, keycloak-db, keycloak)
# -------------------------------------------------------
echo -e "${YELLOW}[Phase 1] Starting core containers (postgres, keycloak-db, keycloak)...${NC}"
cd ../../docker || { echo -e "${RED}Error: Could not switch to docker directory.${NC}"; exit 1; }

# Start core services which are essential for Keycloak availability
docker compose -f docker-compose-todolist.yml --env-file .env up -d postgres keycloak-db keycloak
check_success "Core containers failed to start"
echo -e "${GREEN}Core containers started successfully.${NC}"

# -------------------------------------------------------
# Phase 2: Fetch JWKS from Keycloak
# -------------------------------------------------------
echo -e "${YELLOW}[Phase 2] Fetching JWKS from Keycloak...${NC}"
cd ../examples/todolist || { echo -e "${RED}Error: Could not switch to examples/todolist directory.${NC}"; exit 1; }
echo -e "${YELLOW}Current directory: $(pwd)${NC}"
fetch_jwks || check_success "Failed to fetch JWKS"
echo -e "${GREEN}JWKS fetched successfully.${NC}"

# -------------------------------------------------------
# Phase 3: Launch remaining containers (dataspace-builder, dataspace-app)
# -------------------------------------------------------
echo -e "${YELLOW}[Phase 3] Starting dataspace-builder and dataspace-app containers...${NC}"
cd ../../docker || { echo -e "${RED}Error: Could not switch back to docker directory.${NC}"; exit 1; }
docker compose -f docker-compose-todolist.yml --env-file .env up -d dataspace-builder dataspace-app
check_success "Failed to start dataspace-builder and dataspace-app"
echo -e "${GREEN}Remaining containers started successfully.${NC}"

# Step 7: Check application health
echo -e "${YELLOW}Checking application health...${NC}"
for i in {1..10}; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/actuator/health)
    if [ "$STATUS" == "200" ]; then
        echo -e "${GREEN}Application is running!${NC}"
        echo -e "${GREEN}Database connection parameters:${NC}"
        echo "URL: jdbc:postgresql://localhost:5433/${POSTGRES_DATABASE}?currentSchema=${POSTGRES_SCHEMA_NAME}"
        echo "Username: ${POSTGRES_USERNAME}"
        echo "Password: ${POSTGRES_PASSWORD}"
        echo -e "${GREEN}Useful links:${NC}"
        echo "- Models: http://localhost:8081/actuator/models"
        echo "- GraphiQL: http://localhost:8081/graphiql?path=/models/1/graphql"
        echo -e "${GREEN}Keycloak is available at:${NC}"
        echo "- Admin Console: http://localhost:${KEYCLOAK_PORT:-8090}/admin"
        echo "- Username: ${KEYCLOAK_ADMIN:-admin}"
        echo "- Password: ${KEYCLOAK_ADMIN_PASSWORD}"
        exit 0
    fi
    echo -e "${YELLOW}Waiting for the application to start (${i}/10)...${NC}"
    sleep 5
done

echo -e "${RED}Application failed to start. Check logs:${NC}"
docker compose logs dataspace-app-todolist
exit 1
