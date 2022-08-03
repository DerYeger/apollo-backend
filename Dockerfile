FROM gradle:7.5.0@sha256:45fc04c5a36910fa12729e6e4aeb0e89394f47c6dcd1884b9fe2a6cc26de2363 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:16b401eb4424f9e64d0d90ec3a4a2c0460e5343e37822d1c0c575a1473a82735

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
