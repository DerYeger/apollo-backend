FROM gradle:8.7.0@sha256:b9623b497b2428375312bc511d80da926e4acd908aeab9fceda9e9f40654d753 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:21-alpine@sha256:b5d37df8ee5bb964bb340acca83957f9a09291d07768fba1881f6bfc8048e4f5

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
