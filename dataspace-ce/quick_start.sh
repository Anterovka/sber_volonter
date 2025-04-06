#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'  # No Color

# Set SED_INPLACE depending on OS
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

# Generate a 6-digit password
generate_password() {
    printf "%06d" $RANDOM
}

# Step 1: Read configuration from .env
echo -e "${YELLOW}Reading configuration from docker/.env...${NC}"
if [ ! -f docker/.env ]; then
    echo -e "${RED}Error: docker/.env not found. Create and configure it manually.${NC}"
    exit 1
fi
source docker/.env

# Generate password if not set
if [ -z "$POSTGRES_PASSWORD" ]; then
    POSTGRES_PASSWORD=$(generate_password)
    if grep -q "^POSTGRES_PASSWORD=" docker/.env; then
        sed "${SED_INPLACE[@]}" "s/^POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=${POSTGRES_PASSWORD}/" docker/.env
    else
        echo "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}" >> docker/.env
    fi
    echo -e "${GREEN}Generated PostgreSQL password: ${POSTGRES_PASSWORD}${NC}"
fi

# Step 2: Ensure the custom network exists
echo -e "${YELLOW}Checking if the custom network 'dataspace_network' exists...${NC}"
if ! docker network ls | grep -q "dataspace_network"; then
    echo -e "${YELLOW}Creating custom network 'dataspace_network'...${NC}"
    docker network create dataspace_network || check_success "Failed to create custom network"
else
    echo -e "${GREEN}Custom network 'dataspace_network' already exists.${NC}"
fi

# Step 3: Remove conflicting containers if they exist
CONTAINERS=("postgres" "dataspace-builder" "dataspace-app")
for CONTAINER in "${CONTAINERS[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
        echo -e "${YELLOW}Removing existing container '${CONTAINER}'...${NC}"
        docker rm -f ${CONTAINER} || check_success "Failed to remove conflicting container '${CONTAINER}'"
    else
        echo -e "${GREEN}No conflicting container '${CONTAINER}' found.${NC}"
    fi
done

# Step 4: Update context-child.properties
echo -e "${YELLOW}Updating context-child.properties...${NC}"
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_HOST}|${POSTGRES_HOST}|g" files/resources/src-model/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_PORT}|${POSTGRES_PORT}|g" files/resources/src-model/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_DATABASE}|${POSTGRES_DATABASE}|g" files/resources/src-model/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_USERNAME}|${POSTGRES_USERNAME}|g" files/resources/src-model/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_PASSWORD}|${POSTGRES_PASSWORD}|g" files/resources/src-model/context-child.properties
sed "${SED_INPLACE[@]}" "s|\${POSTGRES_SCHEMA_NAME}|${POSTGRES_SCHEMA_NAME}|g" files/resources/src-model/context-child.properties
check_success "Failed to update context-child.properties"


# Step 5: Detect architecture (for logging purposes only)
ARCH=$(uname -m)
if [[ "$ARCH" == "arm64" ]]; then
    echo -e "${YELLOW}Detected ARM architecture${NC}"
else
    echo -e "${YELLOW}Detected x86 architecture${NC}"
fi

# Step 6: Start Docker Compose with pre-built images
echo -e "${YELLOW}Starting Docker Compose with pre-built images from GitVerse...${NC}"
cd docker || { echo -e "${RED}Error: Cannot switch to docker directory.${NC}"; exit 1; }

# Start containers (no build needed as we're using pre-built images)
docker compose -f docker-compose-quick.yml --env-file .env up -d
check_success "Docker Compose failed"


# Step 7: Check application health
echo -e "${YELLOW}Checking application health...${NC}"
for i in {1..10}; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
    if [ "$STATUS" == "200" ]; then
        echo -e "${GREEN}Application is running!${NC}"
        echo -e "${GREEN}Database connection parameters:${NC}"
        echo "URL: jdbc:postgresql://localhost:5432/${POSTGRES_DATABASE}?currentSchema=${POSTGRES_SCHEMA_NAME}"
        echo "Username: ${POSTGRES_USERNAME}"
        echo "Password: ${POSTGRES_PASSWORD}"
        echo -e "${GREEN}Useful links:${NC}"
        echo "- Models: http://localhost:8080/actuator/models"
        echo "- GraphiQL: http://localhost:8080/graphiql?path=/models/1/graphql"
        exit 0
    fi
    echo -e "${YELLOW}Waiting for the application to start (${i}/10)...${NC}"
    sleep 5
done

echo -e "${RED}Application failed to start. Check logs:${NC}"
docker compose logs dataspace-app
exit 1
