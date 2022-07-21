FROM gradle:7.5.0@sha256:0dc485d4b8cc375c280e4bfe35f14ea2ffeb0fc01d92e7ea7a5b0c43e7f4fe9f AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:f6c38f6c793b67f0ce75915cae66d05ad43b96576dfab8a38543be56a7d71028

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
