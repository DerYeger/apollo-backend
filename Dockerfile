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

FROM eclipse-temurin:17-alpine@sha256:e890b4f91ec8aa40f1537a50a53ab516fc42341c3b5dd608d1aeee2b1cba55b1

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
