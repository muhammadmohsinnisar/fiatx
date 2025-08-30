# 🔄 GitHub Workflows Documentation

This document describes all the automated workflows configured for the FiatX project.

## 📋 Overview

Our CI/CD pipeline includes comprehensive automation for:
- **Code Quality** - Linting, formatting, and static analysis
- **Testing** - Unit tests and coverage reporting
- **Building** - APK generation and validation
- **Security** - Vulnerability scanning
- **Performance** - Build and APK analysis
- **Maintenance** - Dependency updates and monitoring

---

## 🚀 Workflows

### 1. **Android CI** (`android-ci.yml`)
**Triggers:** Push to main/develop/feature branches, PRs to main/develop

**Jobs:**
- **Test** - Runs unit tests with JUnit reporting
- **Lint** - Android lint analysis with HTML reports
- **Build** - Generates debug and release APKs
- **Security** - Trivy vulnerability scanning
- **Code Quality** - SonarCloud analysis (optional)

**Artifacts:**
- Unit test results
- Lint reports
- Debug/Release APKs
- Security scan results

### 2. **Pull Request Checks** (`pr-checks.yml`)
**Triggers:** PR opened/updated to main/develop

**Jobs:**
- **PR Validation** - Tests, lint, and build validation
- **Size Check** - APK size monitoring with 20MB limit
- **Automated Comments** - PR status and APK information

**Features:**
- APK download instructions in PR comments
- Size limit enforcement with warnings
- Build status reporting

### 3. **Release** (`release.yml`)
**Triggers:** Git tags matching `v*` pattern

**Jobs:**
- **Release Creation** - Automated GitHub releases
- **APK Signing** - Signs release APKs (requires secrets)
- **Changelog Generation** - Auto-generated from commits
- **Play Store Upload** - Optional Google Play deployment

**Required Secrets:**
- `SIGNING_KEY` - Base64 encoded keystore
- `ALIAS` - Keystore alias
- `KEY_STORE_PASSWORD` - Keystore password
- `KEY_PASSWORD` - Key password
- `SERVICE_ACCOUNT_JSON` - Google Play service account

### 4. **Code Quality** (`code-quality.yml`)
**Triggers:** Push to main/develop, PRs, weekly schedule

**Jobs:**
- **Detekt** - Kotlin static analysis
- **Ktlint** - Kotlin code formatting
- **Complexity Analysis** - Code metrics and statistics
- **Test Coverage** - Jacoco coverage with Codecov upload

**Reports:**
- Code quality metrics
- Formatting violations
- Complexity statistics
- Coverage percentages

### 5. **Performance Monitoring** (`performance.yml`)
**Triggers:** Push to main, PRs, daily schedule

**Jobs:**
- **APK Analysis** - Size and structure analysis
- **Build Performance** - Clean vs incremental build timing
- **Memory Analysis** - Build memory usage monitoring

**Metrics:**
- APK size tracking
- Build time optimization
- Memory usage patterns
- Performance recommendations

### 6. **Dependency Updates** (`dependency-update.yml`)
**Triggers:** Weekly schedule (Mondays), manual dispatch

**Jobs:**
- **Dependency Check** - Identifies outdated dependencies
- **Gradle Wrapper Update** - Checks for Gradle updates
- **Automated Issues** - Creates issues for available updates

**Features:**
- Weekly dependency scanning
- Gradle version monitoring
- Automated update notifications
- Issue creation for tracking

---

## 🔧 Configuration

### Required Secrets

For full functionality, configure these repository secrets:

```bash
# APK Signing (for releases)
SIGNING_KEY=<base64-encoded-keystore>
ALIAS=<keystore-alias>
KEY_STORE_PASSWORD=<keystore-password>
KEY_PASSWORD=<key-password>

# Google Play Upload (optional)
SERVICE_ACCOUNT_JSON=<service-account-json>

# Code Quality (optional)
SONAR_TOKEN=<sonarcloud-token>
```

### Branch Protection

Recommended branch protection rules for `main`:

- ✅ Require status checks to pass
- ✅ Require branches to be up to date
- ✅ Require pull request reviews
- ✅ Dismiss stale reviews
- ✅ Restrict pushes to matching branches

### Required Status Checks

- `test` - Unit tests must pass
- `lint` - Lint checks must pass
- `build` - Build must succeed
- `pr-validation` - PR validation must pass

---

## 📊 Monitoring & Reports

### Artifacts Generated

Each workflow run generates relevant artifacts:

- **Test Reports** - JUnit XML and HTML reports
- **Lint Results** - Android lint HTML reports
- **APK Files** - Debug and release builds
- **Coverage Reports** - Jacoco XML and HTML
- **Performance Reports** - Build and APK analysis
- **Security Scans** - Vulnerability reports

### Notifications

Workflows provide feedback through:

- **PR Comments** - Build status and APK information
- **Issue Creation** - Dependency updates and security alerts
- **Status Checks** - Pass/fail indicators on PRs
- **Artifact Upload** - Downloadable reports and APKs

---

## 🛠️ Maintenance

### Weekly Tasks (Automated)

- **Monday 9 AM UTC** - Dependency update check
- **Sunday 2 AM UTC** - Code quality analysis
- **Daily 3 AM UTC** - Performance monitoring

### Manual Tasks

- Review and merge dependency update PRs
- Update signing certificates before expiry
- Monitor build performance trends
- Review security scan results

---

## 🚨 Troubleshooting

### Common Issues

**Build Failures:**
- Check Java version compatibility
- Verify Gradle wrapper permissions
- Review dependency conflicts

**Test Failures:**
- Check test environment setup
- Verify mock configurations
- Review API compatibility

**Lint Errors:**
- Run `./gradlew lintDebug` locally
- Fix formatting with `./gradlew ktlintFormat`
- Review Detekt configuration

**APK Size Issues:**
- Enable R8/ProGuard for release builds
- Optimize image resources
- Remove unused dependencies

### Getting Help

1. **Check Workflow Logs** - Detailed error information
2. **Review Artifacts** - Download reports for analysis
3. **Local Reproduction** - Run commands locally first
4. **Issue Creation** - Use bug report template

---

## 📈 Metrics & KPIs

### Build Health

- **Build Success Rate** - Target: >95%
- **Test Coverage** - Target: >80%
- **Build Time** - Target: <5 minutes
- **APK Size** - Target: <20MB

### Code Quality

- **Lint Issues** - Target: 0 errors
- **Code Smells** - Minimize technical debt
- **Complexity** - Monitor cyclomatic complexity
- **Duplication** - Minimize code duplication

### Security

- **Vulnerability Count** - Target: 0 high/critical
- **Dependency Updates** - Keep dependencies current
- **Security Scans** - Regular automated scanning

---

**Last Updated:** $(date)
**Version:** 2.0
**Maintainer:** Mohsin Nisar (mohsinnisarbutt60@gmail.com)
