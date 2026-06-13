Day 1 — Version Compatibility Notes

Final Working Versions
ComponentVersionNotesJava (installed)25.0.1 LTSOracle, released October 2025Java (Maven uses)21.0.10JAVA_HOME points hereMaven3.9.15Apache MavenSpring Boot3.3.5Final stable choiceSpring Cloud2023.0.3Compatible with Spring Boot 3.3.xMaven Compiler Plugin3.13.0Supports Java 21 compilationJWT (jjwt-api)0.12.5io.jsonwebtokenJWT (jjwt-impl)0.12.5io.jsonwebtokenJWT (jjwt-jackson)0.12.5io.jsonwebtokenMySQL8.xLocal installationMySQL Connector/Jmanaged by Spring Bootcom.mysql:mysql-connector-j

Version Conflicts We Hit and Why
Conflict 1 — Maven Using Wrong Java
Problem:
  java -version  → Java 25
  mvn -version   → Java 1.8 (first attempt)
                 → Java 21 (after JAVA_HOME fix)

Root Cause:
  JAVA_HOME was pointing to Java 8 installation
  Maven always uses JAVA_HOME, not system java

Fix:
  Set JAVA_HOME to Java 21 path in
  System Environment Variables → System Variables

Conflict 2 — Maven Compiler Plugin Too Old
Problem:
  maven-compiler-plugin:3.11.0
  Error: invalid flag: --release 25

Root Cause:
  Plugin 3.11.0 did not know about Java 25 release flag

Fix:
  Upgraded to maven-compiler-plugin:3.13.0

Conflict 3 — Class File Version 69
Problem:
  Unsupported class file major version 69

Root Cause:
  Java 25 compiles to class file version 69
  Spring Boot 3.3.5 plugin can only run up to version 65 (Java 21)

  Class File Version Table:
  Java 17 → version 61
  Java 21 → version 65
  Java 25 → version 69

Fix:
  Set maven.compiler.source/target/release = 21 in parent pom.xml
  This forces Maven to compile as Java 21 even though Java 25 is installed

Conflict 4 — Spring Boot vs Spring Cloud Incompatibility
Problem:
  Spring Boot 3.5.0 + Spring Cloud 2023.0.1
  Error: Spring Boot 3.5.0 is not compatible with this Spring Cloud release train

Root Cause:
  Spring Cloud 2023.0.x only supports Spring Boot 3.2.x and 3.3.x

Fix attempt 1:
  Upgraded Spring Cloud to 2024.0.1
  → Still failed (HttpClientProperties$Factory class not found)

Fix attempt 2:
  Downgraded Spring Boot to 3.3.5
  Changed Spring Cloud to 2023.0.3
  → SUCCESS ✅

Conflict 5 — Child Module pom.xml Wrong Parent
Problem:
  api-gateway pom.xml still had:
  <groupId>org.springframework.boot</groupId> in parent block
  Error: Non-resolvable parent POM

Root Cause:
  Spring Initializr generates projects as standalone apps
  pointing directly to Spring Boot as parent
  We need them pointing to our platform parent

Fix:
  Change parent block in every child pom.xml to:
  <groupId>com.resumeanalyzer</groupId>
  <artifactId>resume-analyzer-platform</artifactId>
  <relativePath>../pom.xml</relativePath>

Spring Boot ↔ Spring Cloud Compatibility Table
Spring Boot 3.2.x  →  Spring Cloud 2023.0.x
Spring Boot 3.3.x  →  Spring Cloud 2023.0.x  ✅ We use this
Spring Boot 3.4.x  →  Spring Cloud 2024.0.x
Spring Boot 3.5.x  →  Spring Cloud 2024.0.x
Reference: https://spring.io/projects/spring-cloud#overview

Java Class File Version Table
Java 17  →  Class file version 61
Java 21  →  Class file version 65  ✅ We compile to this
Java 25  →  Class file version 69  ✅ Our runtime

Key Rules Learned
Rule 1:
  Always check mvn -version before starting
  JAVA_HOME controls which Java Maven uses
  NOT the java on your PATH

Rule 2:
  Spring Boot version and Spring Cloud version
  must be from compatible release trains
  Always check the compatibility table

Rule 3:
  You can install Java 25 but compile to Java 21
  Code compiled at 21 runs perfectly on Java 25 runtime
  This is called bytecode compatibility

Rule 4:
  Spring Initializr always generates standalone projects
  Always replace the parent block when using multi-module setup

Rule 5:
  When hitting version conflicts always clean first
  mvn clean removes old compiled class files
  that may carry the wrong Java version