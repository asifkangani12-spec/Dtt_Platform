# DTT Platform — Gradle Multi-Module Project

Consolidated from 5 standalone Spring Boot projects into one unified Gradle multi-module build.

## Module Map

| Module | Migrated From | Base Package | Responsibility |
|--------|--------------|--------------|---------------|
| `generic-services` | Generic-Services | `com.dtt` | App config, cards, consent, wallet, Firebase push, PDF |
| `onboarding` | onboarding | `ug.daes.onboarding` | Subscriber lifecycle, OTP, device, MinIO storage |
| `registration-authority` | RegistrationAuthority | `ug.daes.ra` | PKI cert lifecycle, RA operations |
| `organization` | Organization | `ug.daes.organization` | Org management, agent certs, dashboard |
| `onboarding-transaction-handler` | OnBoardingTransactionHandler | `ug.daes.OnBoardingTransactionHandler` | Orchestration, MOSIP, payment, security |
| `app` | _(new)_ | `com.dtt.platform` | Single entry point, produces fat JAR |

## Project Structure

```
dtt-platform/
├── settings.gradle                        ← declares all modules
├── build.gradle                           ← shared config for all subprojects
├── gradle/
│   ├── libs.versions.toml                 ← single source of truth for versions
│   └── wrapper/gradle-wrapper.properties
├── app/                                   ← ONLY module with spring-boot plugin
│   └── src/main/
│       ├── java/com/dtt/platform/DttPlatformApplication.java
│       └── resources/
│           ├── application.properties         ← base config
│           ├── application-local.properties   ← local dev
│           └── application-uaeid.properties   ← staging/production
├── generic-services/
├── onboarding/
├── registration-authority/
├── organization/
└── onboarding-transaction-handler/
```

## Build & Run

```bash
# Build everything
./gradlew clean build

# Run the application
./gradlew :app:bootRun

# Run with a specific profile
./gradlew :app:bootRun --args='--spring.profiles.active=uaeid'

# Produce executable JAR
./gradlew :app:bootJar
# JAR output: app/build/libs/dtt-platform.jar
java -jar app/build/libs/dtt-platform.jar --spring.profiles.active=uaeid

# Build a single module only
./gradlew :onboarding:build

# View full dependency tree
./gradlew :app:dependencies --configuration runtimeClasspath
```
