FROM gradle:8.0.1@sha256:f4f2d83cdb8e2159e0ccdf1d743db83054b1cde8b808bfef6d10f1fd8951df90 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:172a2a9dac65deb5d53d8ff975f45645d6b82c37e4a7b6ead32e8ad2eb7d7e0b

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
