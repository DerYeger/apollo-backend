FROM gradle:8.3.0@sha256:461018d754af5e85ca08b8f38e5f583d65ebadf83be8ad01f3bff03740ccef4f AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:17-alpine@sha256:fe702d6a9b2d0855f29154512358cd5c0c866b8b16544589e254a97743304d1a

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
