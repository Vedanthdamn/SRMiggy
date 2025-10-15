# CI/CD Setup Documentation

## Overview

This document describes the Continuous Integration and Continuous Deployment (CI/CD) setup for the SRMiggy project using GitHub Actions.

## Problem Solved

The project was experiencing "build failure" reports, which was caused by the absence of an automated build and test pipeline. Without CI/CD, developers couldn't easily verify that their changes would build successfully before merging.

## Solution Implemented

A comprehensive GitHub Actions workflow has been added to automatically build and test both the backend and frontend components on every push and pull request.

## Workflow Configuration

**File**: `.github/workflows/build.yml`

### Trigger Events

The workflow runs automatically on:
- **Push events** to `main` and `develop` branches
- **Pull request events** targeting `main` and `develop` branches

### Jobs

#### 1. Backend Build Job

**Environment**: Ubuntu Latest  
**Java Version**: 17 (Temurin distribution)

**Steps**:
1. **Checkout code** - Retrieves the repository code
2. **Set up JDK 17** - Installs Java 17 with Maven caching
3. **Build with Maven** - Compiles the code and creates JAR file
4. **Run tests** - Executes unit tests
5. **Upload artifact** - Stores the built JAR file for 7 days

**Command**: `mvn clean package -DskipTests` followed by `mvn test`

#### 2. Frontend Build Job

**Environment**: Ubuntu Latest  
**Node Version**: 20

**Steps**:
1. **Checkout code** - Retrieves the repository code
2. **Set up Node.js** - Installs Node.js 20 with npm caching
3. **Install dependencies** - Runs `npm ci` for clean install
4. **Run linting** - Executes ESLint for code quality checks
5. **Build frontend** - Creates production build with Vite
6. **Upload artifact** - Stores the build output for 7 days

**Commands**: `npm ci`, `npm run lint`, `npm run build`

## Build Status Badge

A build status badge has been added to the README.md that shows the current status of the build:

```markdown
[![Build and Test](https://github.com/Vedanthdamn/SRMiggy/actions/workflows/build.yml/badge.svg)](https://github.com/Vedanthdamn/SRMiggy/actions/workflows/build.yml)
```

This badge will show:
- ✅ Green "passing" when builds succeed
- ❌ Red "failing" when builds fail

## Benefits

### For Developers
- **Instant feedback** on code changes
- **Early detection** of build failures
- **Consistent environment** for testing
- **Confidence** in merging code

### For the Project
- **Quality assurance** through automated testing
- **Build artifacts** available for download
- **Documentation** of build history
- **Professional appearance** with status badges

## Local Build Verification

Before pushing code, developers can verify builds locally:

### Backend
```bash
cd backend
mvn clean package
mvn test
```

### Frontend
```bash
cd frontend
npm install
npm run lint
npm run build
```

## Viewing Build Results

1. **GitHub Actions Tab**: Navigate to the repository's Actions tab
2. **Pull Requests**: See build status directly on PR pages
3. **Commits**: View build status on individual commits
4. **README Badge**: Quick status check from the main page

## Troubleshooting

### Backend Build Fails

**Common causes**:
- Compilation errors in Java code
- Failing unit tests
- Missing dependencies in pom.xml
- Incompatible Java version

**Solution**: Run `mvn clean package` locally to reproduce and fix

### Frontend Build Fails

**Common causes**:
- ESLint errors (not warnings)
- Missing npm dependencies
- Build errors in Vite
- Syntax errors in JavaScript/JSX

**Solution**: Run `npm run lint && npm run build` locally to reproduce and fix

### Workflow Configuration Issues

**Common causes**:
- Invalid YAML syntax
- Incorrect paths
- Missing or outdated GitHub Actions

**Solution**: 
- Validate YAML syntax
- Check working-directory paths
- Update action versions if needed

## Build Performance

### Current Build Times (Approximate)

- **Backend**: 1-2 minutes (with Maven cache)
- **Frontend**: 30-60 seconds (with npm cache)
- **Total**: 2-3 minutes for complete validation

### Caching

Both jobs use caching to speed up builds:
- **Backend**: Maven dependencies cached by setup-java action
- **Frontend**: npm packages cached by setup-node action

## Artifacts

Build artifacts are stored for 7 days after each successful build:

### Backend Artifacts
- **Name**: backend-jar
- **Contents**: `*.jar` files from backend/target/
- **Use**: Deployment or testing

### Frontend Artifacts
- **Name**: frontend-dist
- **Contents**: Complete frontend/dist/ folder
- **Use**: Deployment to hosting services

## Future Enhancements

Possible improvements to the CI/CD pipeline:

1. **Deploy to staging** - Automatic deployment to staging environment
2. **Code coverage reports** - Integration with coverage tools
3. **Security scanning** - Dependency vulnerability checks
4. **Performance testing** - Load testing in CI
5. **Docker builds** - Containerized deployment artifacts
6. **Multi-environment testing** - Test against different databases

## Maintenance

### Updating Dependencies

The workflow uses the following action versions:
- `actions/checkout@v4`
- `actions/setup-java@v4`
- `actions/setup-node@v4`
- `actions/upload-artifact@v4`

Check for updates periodically and update version numbers as needed.

### Modifying Workflow

To modify the workflow:
1. Edit `.github/workflows/build.yml`
2. Commit and push changes
3. The workflow will automatically use the new configuration

## Conclusion

The CI/CD setup ensures that all code changes are automatically built and tested, preventing build failures from reaching the main branches. This provides confidence to developers and maintains code quality throughout the development lifecycle.

---

**Setup Date**: October 15, 2025  
**Status**: ✅ Active and Operational  
**Maintainer**: Development Team
