FROM gradle:8.6.0@sha256:27ed98487dd9c155d555955084dfd33f32d9f7ac5a90a79b1323ab002a1a8b6e AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:21-alpine@sha256:6da40af213861c1b9cc475affa011d395d4dbcf0cb031b6a5d05a658fd9e3c5e

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
