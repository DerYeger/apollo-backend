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

FROM adoptopenjdk/openjdk16:alpine@sha256:402e5e58f5e364af9b05218afe43e813619359eaf517f3dd1a1b552c1ab4aa88

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
