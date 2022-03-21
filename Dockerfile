FROM gradle:7.4.1@sha256:2cbc28de0730a6c68cfaf18d28138dcfb8b7d8dfc966eb71fcfa7621ac6766e0 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:2d2add9fb4fc65c90fa52c3d38825f680ca22cbc5c22e8119ffb313d6c2b383e

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
