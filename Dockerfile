FROM gradle:7.3.1@sha256:4c6efa1d6a79c15a6a03f8396f0779f294a647ee75d325ae159fef9c778e35ad AS BUILDER

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
