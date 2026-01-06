# CampusMasterBackend

# Demarrer serveur spring boot
./mvnw spring-boot:run



#spring.application.name=campusMaster

# PostgreSQL Configuration
#spring.datasource.url=jdbc:postgresql://localhost:5432/campusmaster
#spring.datasource.username=admin
#spring.datasource.password=
#spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
#spring.jpa.hibernate.ddl-auto=update
#spring.jpa.show-sql=true
#spring.jpa.properties.hibernate.format_sql=true
#spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Désactiver Spring Security temporairement pour les tests
#spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
#spring.security.user.name=admin
#spring.security.user.password=admin

# JWT Configuration
#jwt.secret=your-super-secret-key-min-256-bits-campusmaster-2025
#jwt.expiration=3600000
#jwt.refresh-expiration=86400000

# ===============================
# Application
# ===============================
spring.application.name=campusMaster


# ===============================
# PostgreSQL (Docker)
# ===============================
spring.datasource.url=jdbc:postgresql://postgres:5432/campusmaster
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver


# ===============================
# JPA / Hibernate
# ===============================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect


# ===============================
# Security
# ===============================
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration


# ===============================
# JWT
# ===============================
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
jwt.refresh-expiration=86400000
