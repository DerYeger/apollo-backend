FROM gradle:8.3.0@sha256:f2973784898e292638ecd16af4f9d2f7e32bfda5d07cc74248a152da926dbc41 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM eclipse-temurin:17-alpine@sha256:61bf57c1550b428c4e0e49339ff7e23964fed193b50734bf1f8854b3fd19f765

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
