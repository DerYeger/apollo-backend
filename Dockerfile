FROM gradle:8.4.0@sha256:9fde0212a97ab5c96e17797bba1f4f63c223a5ca04131d8dbafce038c53c4eb3 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:21-alpine@sha256:001dfe1c179b3f315bd6549ad1fe94fd7204984319bd3c0f3b385b5188cb18b8

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
