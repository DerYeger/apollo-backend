FROM gradle:7.4.2@sha256:1eac4e757c66004d81d74eb94cb99c9d11de1b2b35c664793bfd4d60d467c6b8 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:ff2b0e88a9ed96f818bd5a3b016e32a57b604f9c6cd1d5f4253398581723be8c

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
