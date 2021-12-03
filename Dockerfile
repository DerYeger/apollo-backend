FROM gradle:7.3.1@sha256:d5d7c5da838a491bb06a2d8c914546c5f0c6ad2aeddce4e633810191b8b38cf1 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:bfbd644a7134a25c9033c8bd7b8122679df1f1e0ba3b3efa11e7ca54a268f0ab

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
