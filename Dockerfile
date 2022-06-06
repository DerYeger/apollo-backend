FROM gradle:7.4.2@sha256:14223d3b6920e7ee2a17f36b20d787ce91b9dbab6d13851e718734ed354ec762 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:8d90d4e6c0c9d8b4f46abbfd673c25b1f7747f254dfb8f40a58cb75f16327ecd

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
