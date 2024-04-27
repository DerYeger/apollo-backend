FROM gradle:8.7.0@sha256:4301b1887d736d5b90c20b2f65593ee0d545a543a41d117e5a2df36b3bc22b88 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:21-alpine@sha256:ebfc28d35b192c55509e3c7cc597d91136528f1a9d3261965b44663af9eb4b4b

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
