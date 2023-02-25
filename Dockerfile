FROM gradle:8.0.1@sha256:0b1ac10c5beda68b6fb9c1b9d789da2a27851403f6511bbab50d1ab4e96cc6e1 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:60717c2a446418e77531e516011a37170c65d8515aaec3c68611563b1cd0b8a8

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
