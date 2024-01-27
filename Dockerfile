FROM gradle:8.5.0@sha256:c216f357d9e4fb3b43f318665d166e05fe9ba8dea4f962c6346a1b4d197e80e7 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:21-alpine@sha256:6da40af213861c1b9cc475affa011d395d4dbcf0cb031b6a5d05a658fd9e3c5e

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
