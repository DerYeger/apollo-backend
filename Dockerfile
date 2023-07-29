FROM gradle:8.2.1@sha256:fa5a07f9d3a738e181c941b3896974a01659f9b28834531f38dd6d321bd3a039 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:17-alpine@sha256:3ecc5edd648f5d9c92e53e8eb6361cfcfaf626220f8308332f239dfb03418c1c

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
