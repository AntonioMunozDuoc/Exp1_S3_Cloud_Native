# Upgrade Plan: Exp1_S3_Cloud (20260607183953)

- **Generated**: 2026-06-07 18:45:00
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 21.0.6: C:\Program Files\Java\jdk-21\bin (current project JDK, used by steps 1, 3, 5)

**Build Tools**
- Maven 3.9.14: C:\Workspace\apache-maven-3.9.14\bin
- Maven Wrapper: 3.9.15 (`guiaservice/.mvn/wrapper/maven-wrapper.properties`, `pedidoservice/.mvn/wrapper/maven-wrapper.properties`)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260607183953
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade Java runtime target to Java 21 (latest LTS)

## Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
| --------------------- | ------- | -------------- | ---------------- |
| Java | 17 / 21 | 21 | User requested latest LTS runtime across modules |
| Spring Boot | 3.5.14 | 3.5.14 | Current version is already compatible with Java 21 |
| Maven Wrapper | 3.9.15 | 3.9.0 | Recommended for Java 21; already satisfied |
| maven-compiler-plugin | managed by Spring Boot BOM | 3.11+ recommended | Current managed plugin is compatible with Java 21 |

## Derived Upgrades

- `guiaservice` must align its compile/runtime target with the user-requested Java 21 runtime.
- The `guiaservice` Dockerfile must be updated to use Java 21 build and runtime base images.
- CI/CD documentation/comments should reflect that both services now target Java 21.

## Impact Analysis

### Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|------------|---------|--------|--------|--------|
| guiaservice/pom.xml | `<java.version>` | 17 | upgrade | 21 | Align guiaservice with latest LTS runtime |

### Source Code Changes

| File | Location | Current | Required Change | Reason |
|------|----------|---------|----------------|--------|
| (none) | | | No source code changes required | Java 21 compatibility is handled by build/runtime settings |

### Configuration Changes

| File | Property/Setting | Current | Required Change | Reason |
|------|------------------|---------|----------------|--------|
| guiaservice/Dockerfile | base images | eclipse-temurin:17-jdk-alpine, eclipse-temurin:17-jre-alpine | eclipse-temurin:21-jdk-alpine, eclipse-temurin:21-jre-alpine | Update runtime/container environment to Java 21 |
| .github/workflows/main.yml | comment | "guia service compiles inside Dockerfile with JDK 17" | update to mention JDK 21 | Keep CI documentation accurate |

### CI/CD Changes

| File | Location | Current | Required Change |
|------|----------|---------|----------------|
| .github/workflows/main.yml | comment block | JDK 17 note for guiaservice | update to JDK 21 note |

### Risks & Warnings

- **Guiaservice runtime image update**: switching container base images from Java 17 to Java 21 may expose compatibility issues in the service or its dependencies. Mitigation: compile and test `guiaservice` with Java 21 in step 3 and step 5.
- **No base JDK 17 installed locally**: baseline verification on the original Java 17 runtime is not available in this environment. Mitigation: rely on current JDK 21 validation and note baseline skip.

## Upgrade Steps

- Step 1: Verify Java 21 and Maven environment
  - Rationale: Ensure the target runtime and build tools are already available so the upgrade can proceed without toolchain installation.
  - Changes to Make: None; confirm local environment state and wrapper version.
  - Verification: `mvn -version`

- Step 2: Baseline verification (skipped if Java 17 unavailable)
  - Rationale: Preserve behavior by validating the pre-upgrade state if the original JDK is installed.
  - Changes to Make: None if skipped.
  - Verification: skipped due to missing JDK 17 locally.

- Step 3: Upgrade `guiaservice` runtime target to Java 21
  - Rationale: Align the remaining Java 17 module with the latest LTS runtime and container base images.
  - Changes to Make: apply Dependency Changes, Configuration Changes, and CI/CD comment update.
  - Verification: `./guiaservice/mvnw -f guiaservice/pom.xml clean test-compile -q`

- Step 4: CVE validation and fix
  - Rationale: Verify direct dependencies for known vulnerabilities and fix any issues introduced or exposed by the upgrade.
  - Changes to Make: scan direct dependencies, upgrade patched versions as needed, keep minimal patch-level updates.
  - Verification: `./guiaservice/mvnw -f guiaservice/pom.xml clean test-compile -q && ./pedidoservice/mvnw -f pedidoservice/pom.xml clean test-compile -q`

- Step 5: Final validation across both services
  - Rationale: Confirm that the full upgrade succeeds for all modules and that test suites pass with Java 21.
  - Changes to Make: any fixes needed from step 4 or remaining Java 21 compatibility issues.
  - Verification: `./guiaservice/mvnw -f guiaservice/pom.xml clean test && ./pedidoservice/mvnw -f pedidoservice/pom.xml clean test`
