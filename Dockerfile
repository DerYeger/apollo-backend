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

FROM eclipse-temurin:17-alpine@sha256:039f727ed86402f37524b5d01a129947e2f061d5856901d07d73a454e689bb13

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
