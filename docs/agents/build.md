# Build & Tooling

## build.gradle Key Facts

- **Group:** `com.stavre`, **Version:** `0.0.1-SNAPSHOT`
- **Java toolchain:** Java 25 (`languageVersion = JavaLanguageVersion.of(25)`)
- **Spring Boot:** 4.0.3

### Dependencies

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity6'
runtimeOnly    'com.h2database:h2'
compileOnly    'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'

testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'   ← required for .with(user(...)), csrf()
```

**Note:** `spring-security-test` is an explicit dependency. In Spring Boot 4.x it is NOT automatically pulled in by `spring-boot-starter-test`.

### Test Runtime

```groovy
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
```

Required for JUnit 5 test discovery.

## Checkstyle

- **Version:** 13.2.0
- **Config file:** `config/checkstyle/checkstyle.xml`
- **Style:** Google Java Style Guide
- **Applies to:** both `main` and `test` sources
- **Violations fail the build** (`ignoreFailures = false`)

Key rules in effect:
- `CustomImportOrder` — static imports first, then all non-static (no blank lines within each group)
- `VariableDeclarationUsageDistance` — max 3 lines between declaration and first use
- `AbbreviationAsWordInName` — `ignoreStaticFinal=true` so `UPPER_SNAKE_CASE` constants are allowed
- Line length, indentation, braces, etc. per Google style

## PMD

- **Version:** 7.16.0
- **Config file:** `pmd/pmd-rules.xml` (custom ruleset, NOT inline in build.gradle)
- **Minimum priority:** 5 (all priorities included)
- **Console output:** enabled
- **Violations fail the build**

```groovy
pmd {
    ignoreFailures = false
    consoleOutput = true
    toolVersion = "7.16.0"
    rulesMinimumPriority = 5
    ruleSets = []                          // must be empty when using ruleSetFiles
    ruleSetFiles = files("pmd/pmd-rules.xml")
}
```

`pmd/pmd-rules.xml` inherits `category/java/bestpractices.xml` with:
- `UnitTestShouldIncludeAssert` excluded (MockMvc andExpect() not recognized)
- `UnitTestContainsTooManyAsserts` re-included with `maximumAsserts=5`

## JaCoCo

- **Threshold:** 70% line coverage on `com.stavre.tinyurl.*`
- **Enabled:** `jacocoTestCoverageVerification.enabled = true`
- Coverage report generated at `build/reports/jacoco/`
- Runs as part of `./gradlew check`

```groovy
jacocoTestCoverageVerification {
    enabled = true
    violationRules {
        rule {
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.70
            }
        }
    }
}
```

## Gradle Task Order

`check` runs: `compileJava` → `checkstyleMain` → `compileTestJava` → `checkstyleTest` → `test` → `jacocoTestReport` → `jacocoTestCoverageVerification` → `pmdMain` → `pmdTest`

If Checkstyle fails, the test task still runs (they are parallel-capable). Both failures are reported at the end.

## Lombok

Used for `@Data`, `@RequiredArgsConstructor`, `@AllArgsConstructor`, `@NoArgsConstructor` on entity classes. Generates getters, setters, constructors at compile time. No manual getter/setter methods exist in entity source files.
