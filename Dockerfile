FROM gradle:7.5.0@sha256:45fc04c5a36910fa12729e6e4aeb0e89394f47c6dcd1884b9fe2a6cc26de2363 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:fd0b50a7111cd621c3965910c5528f6b3548471876cda1d4028a0848128f2d18

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
