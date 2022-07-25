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

FROM adoptopenjdk/openjdk16:alpine@sha256:5e492085612b3e9acd7b6d20438891393567b15160cad05a090e2f4f5af209e7

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
