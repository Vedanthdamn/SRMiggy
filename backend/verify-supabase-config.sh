#!/bin/bash

# Supabase Configuration Verification Script
# This script verifies that the Supabase PostgreSQL configuration is correct

set -e

echo "========================================="
echo "  Supabase Configuration Verification"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Read application.properties
PROPS_FILE="src/main/resources/application.properties"

if [ ! -f "$PROPS_FILE" ]; then
    echo -e "${RED}✗ application.properties not found${NC}"
    exit 1
fi

echo "📋 Checking configuration in application.properties..."
echo ""

# Extract configuration values
DB_URL=$(grep "spring.datasource.url=" "$PROPS_FILE" | cut -d'=' -f2-)
DB_USERNAME=$(grep "spring.datasource.username=" "$PROPS_FILE" | cut -d'=' -f2-)
DB_DRIVER=$(grep "spring.datasource.driver-class-name=" "$PROPS_FILE" | cut -d'=' -f2-)
JPA_DIALECT=$(grep "spring.jpa.database-platform=" "$PROPS_FILE" | cut -d'=' -f2-)
DDL_AUTO=$(grep "spring.jpa.hibernate.ddl-auto=" "$PROPS_FILE" | cut -d'=' -f2-)

echo "Database URL: $DB_URL"
echo "Database Username: $DB_USERNAME"
echo "Database Driver: $DB_DRIVER"
echo "JPA Dialect: $JPA_DIALECT"
echo "Hibernate DDL Auto: $DDL_AUTO"
echo ""

# Verify expected values
EXPECTED_URL="jdbc:postgresql://db.awbmgncjszicqegrpsxj.supabase.co:5432/postgres"
EXPECTED_USERNAME="postgres"
EXPECTED_DRIVER="org.postgresql.Driver"

if [ "$DB_URL" = "$EXPECTED_URL" ]; then
    echo -e "${GREEN}✓ Database URL is correctly configured${NC}"
else
    echo -e "${RED}✗ Database URL mismatch${NC}"
    echo "  Expected: $EXPECTED_URL"
    echo "  Got: $DB_URL"
fi

if [ "$DB_USERNAME" = "$EXPECTED_USERNAME" ]; then
    echo -e "${GREEN}✓ Database username is correctly configured${NC}"
else
    echo -e "${RED}✗ Database username mismatch${NC}"
fi

if [ "$DB_DRIVER" = "$EXPECTED_DRIVER" ]; then
    echo -e "${GREEN}✓ PostgreSQL driver is correctly configured${NC}"
else
    echo -e "${RED}✗ PostgreSQL driver mismatch${NC}"
fi

echo ""
echo "📦 Checking PostgreSQL dependency in pom.xml..."
if grep -q "org.postgresql" pom.xml; then
    echo -e "${GREEN}✓ PostgreSQL dependency found in pom.xml${NC}"
else
    echo -e "${RED}✗ PostgreSQL dependency not found in pom.xml${NC}"
fi

echo ""
echo "🔧 Checking HikariCP configuration..."
if grep -q "spring.datasource.hikari" "$PROPS_FILE"; then
    echo -e "${GREEN}✓ HikariCP connection pool is configured${NC}"
    echo "  Maximum Pool Size: $(grep 'hikari.maximum-pool-size' "$PROPS_FILE" | cut -d'=' -f2-)"
    echo "  Minimum Idle: $(grep 'hikari.minimum-idle' "$PROPS_FILE" | cut -d'=' -f2-)"
    echo "  Connection Timeout: $(grep 'hikari.connection-timeout' "$PROPS_FILE" | cut -d'=' -f2-)"
else
    echo -e "${YELLOW}⚠ HikariCP connection pool not configured${NC}"
fi

echo ""
echo "🏗️  Building project..."
if mvn clean compile -DskipTests > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Project builds successfully${NC}"
else
    echo -e "${RED}✗ Project build failed${NC}"
    echo "Run 'mvn clean compile' to see detailed errors"
fi

echo ""
echo "========================================="
echo "  Configuration Summary"
echo "========================================="
echo ""
echo "The application has been configured to use:"
echo "  • Supabase PostgreSQL database"
echo "  • Host: db.awbmgncjszicqegrpsxj.supabase.co"
echo "  • Port: 5432"
echo "  • Database: postgres"
echo "  • Username: postgres"
echo "  • Password: ******** (configured)"
echo ""
echo "To run the application:"
echo "  cd backend"
echo "  mvn spring-boot:run"
echo ""
echo "Note: The application will attempt to connect to the"
echo "Supabase database on startup. Ensure the database is"
echo "accessible from your network."
echo ""
echo "Expected behavior on successful connection:"
echo "  • Tables will be automatically created/updated"
echo "  • Application will start on port 8080"
echo "  • API endpoints will be available at http://localhost:8080/api/"
echo ""
