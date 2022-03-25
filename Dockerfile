FROM gradle:7.4.1@sha256:2cbc28de0730a6c68cfaf18d28138dcfb8b7d8dfc966eb71fcfa7621ac6766e0 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:8815ab17eaa92b1f8f1831791bb18b3250e564d8a5c47ec6152741f7bc59ec86

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
