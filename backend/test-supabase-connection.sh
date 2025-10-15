#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Supabase Connection Test Tool${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  No .env file found!${NC}"
    echo -e "Creating .env from .env.example...\n"
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo -e "${YELLOW}📝 Please edit .env file with your Supabase credentials${NC}"
        echo -e "   1. Open .env file"
        echo -e "   2. Replace 'your-project-ref-here' with your Supabase project reference"
        echo -e "   3. Replace 'your-supabase-password-here' with your database password"
        echo -e "\n${RED}❌ Cannot proceed without valid credentials${NC}\n"
        exit 1
    else
        echo -e "${RED}❌ .env.example file not found${NC}\n"
        exit 1
    fi
fi

# Load environment variables
echo -e "${BLUE}📋 Loading configuration from .env...${NC}"
export $(grep -v '^#' .env | xargs)

# Validate configuration
echo -e "\n${BLUE}🔍 Validating configuration...${NC}\n"

CONFIG_VALID=true

# Check SUPABASE_PROJECT_REF
if [ -z "$SUPABASE_PROJECT_REF" ] || [ "$SUPABASE_PROJECT_REF" = "your-project-ref-here" ]; then
    echo -e "${RED}❌ SUPABASE_PROJECT_REF is not configured${NC}"
    CONFIG_VALID=false
else
    echo -e "${GREEN}✓ SUPABASE_PROJECT_REF: $SUPABASE_PROJECT_REF${NC}"
fi

# Check SUPABASE_DB_PASSWORD
if [ -z "$SUPABASE_DB_PASSWORD" ] || [ "$SUPABASE_DB_PASSWORD" = "your-supabase-password-here" ]; then
    echo -e "${RED}❌ SUPABASE_DB_PASSWORD is not configured${NC}"
    CONFIG_VALID=false
else
    echo -e "${GREEN}✓ SUPABASE_DB_PASSWORD: ********${NC}"
fi

# Check DATABASE_URL
if [ -z "$DATABASE_URL" ] || [[ "$DATABASE_URL" == *"your-project-ref-here"* ]]; then
    echo -e "${RED}❌ DATABASE_URL contains placeholder values${NC}"
    CONFIG_VALID=false
else
    echo -e "${GREEN}✓ DATABASE_URL: ${DATABASE_URL}${NC}"
fi

# Check SPRING_PROFILES_ACTIVE
if [ "$SPRING_PROFILES_ACTIVE" != "supabase" ]; then
    echo -e "${YELLOW}⚠️  SPRING_PROFILES_ACTIVE is not set to 'supabase'${NC}"
    echo -e "   Current value: ${SPRING_PROFILES_ACTIVE:-empty}"
    CONFIG_VALID=false
else
    echo -e "${GREEN}✓ SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE${NC}"
fi

if [ "$CONFIG_VALID" = false ]; then
    echo -e "\n${RED}❌ Configuration validation failed!${NC}"
    echo -e "${YELLOW}Please update your .env file with valid Supabase credentials${NC}\n"
    exit 1
fi

echo -e "\n${GREEN}✓ Configuration validation passed!${NC}\n"

# Test database connection using psql if available
echo -e "${BLUE}🔌 Testing database connection...${NC}\n"

# Extract connection details
DB_HOST="db.${SUPABASE_PROJECT_REF}.supabase.co"
DB_PORT="5432"
DB_NAME="postgres"
DB_USER="${DB_USERNAME:-postgres}"

# Check if psql is available
if command -v psql &> /dev/null; then
    echo -e "${BLUE}Testing connection with psql...${NC}"
    
    # Test connection
    PGPASSWORD="$SUPABASE_DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT version();" &> /dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Database connection successful!${NC}\n"
        
        # Get PostgreSQL version
        PG_VERSION=$(PGPASSWORD="$SUPABASE_DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -c "SELECT version();" 2>/dev/null | head -1)
        echo -e "${BLUE}PostgreSQL Version:${NC} $PG_VERSION\n"
        
        # Check if tables exist
        echo -e "${BLUE}Checking for SRMiggy tables...${NC}"
        TABLES=$(PGPASSWORD="$SUPABASE_DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -c "SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename;" 2>/dev/null)
        
        if [ -z "$TABLES" ]; then
            echo -e "${YELLOW}⚠️  No tables found in database${NC}"
            echo -e "   You need to run the schema SQL script:"
            echo -e "   1. Go to Supabase Dashboard > SQL Editor"
            echo -e "   2. Run backend/src/main/resources/supabase-schema.sql"
            echo -e "   3. Run backend/src/main/resources/supabase-seed-data.sql (optional)\n"
        else
            echo -e "${GREEN}✓ Found tables:${NC}"
            echo "$TABLES" | while read -r table; do
                if [ ! -z "$table" ]; then
                    echo -e "  - $table"
                fi
            done
            echo ""
        fi
    else
        echo -e "${RED}❌ Database connection failed!${NC}"
        echo -e "\n${YELLOW}Possible issues:${NC}"
        echo -e "  1. Incorrect project reference or password"
        echo -e "  2. Network/firewall blocking port 5432"
        echo -e "  3. Supabase project not accessible"
        echo -e "  4. IP address not whitelisted in Supabase (if IP restrictions enabled)\n"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠️  psql not installed, skipping direct database test${NC}"
    echo -e "   Install PostgreSQL client to test connection directly\n"
fi

# Test with Java application
echo -e "${BLUE}🚀 Testing with Spring Boot application...${NC}\n"
echo -e "Starting backend with Supabase profile..."
echo -e "${YELLOW}This may take a few moments...${NC}\n"

# Run Spring Boot with Supabase profile
mvn spring-boot:run -Dspring-boot.run.profiles=supabase > /tmp/srmiggy-test.log 2>&1 &
SPRING_PID=$!

# Wait for application to start (max 60 seconds)
echo -n "Waiting for application to start"
for i in {1..60}; do
    if grep -q "Started SrmiggyApplication" /tmp/srmiggy-test.log 2>/dev/null; then
        echo -e "\n${GREEN}✓ Application started successfully!${NC}\n"
        
        # Test API endpoint
        echo -e "${BLUE}Testing API endpoint...${NC}"
        sleep 2
        
        RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/vendors)
        
        if [ "$RESPONSE" = "200" ]; then
            echo -e "${GREEN}✓ API endpoint responding correctly (HTTP $RESPONSE)${NC}\n"
            
            # Get vendor count
            VENDOR_COUNT=$(curl -s http://localhost:8080/api/vendors | jq '. | length' 2>/dev/null)
            if [ ! -z "$VENDOR_COUNT" ]; then
                echo -e "${GREEN}✓ Found $VENDOR_COUNT vendors in database${NC}\n"
            fi
        else
            echo -e "${RED}❌ API endpoint returned HTTP $RESPONSE${NC}\n"
        fi
        
        # Show last few log lines
        echo -e "${BLUE}Recent log entries:${NC}"
        tail -20 /tmp/srmiggy-test.log | grep -v "Progress" | tail -10
        
        # Stop the application
        echo -e "\n${BLUE}Stopping application...${NC}"
        kill $SPRING_PID 2>/dev/null
        wait $SPRING_PID 2>/dev/null
        
        echo -e "\n${GREEN}========================================${NC}"
        echo -e "${GREEN}  ✓ All tests passed!${NC}"
        echo -e "${GREEN}  Your Supabase connection is working!${NC}"
        echo -e "${GREEN}========================================${NC}\n"
        exit 0
    fi
    
    # Check for errors
    if grep -q "HikariPool.*Exception" /tmp/srmiggy-test.log 2>/dev/null || \
       grep -q "Communications link failure" /tmp/srmiggy-test.log 2>/dev/null || \
       grep -q "Connection refused" /tmp/srmiggy-test.log 2>/dev/null; then
        echo -e "\n${RED}❌ Database connection failed!${NC}\n"
        echo -e "${YELLOW}Error details:${NC}"
        grep -A 5 "Exception\|Error" /tmp/srmiggy-test.log | tail -20
        kill $SPRING_PID 2>/dev/null
        wait $SPRING_PID 2>/dev/null
        exit 1
    fi
    
    echo -n "."
    sleep 1
done

echo -e "\n${RED}❌ Application failed to start within 60 seconds${NC}"
echo -e "${YELLOW}Last 20 log lines:${NC}"
tail -20 /tmp/srmiggy-test.log
kill $SPRING_PID 2>/dev/null
wait $SPRING_PID 2>/dev/null
exit 1
