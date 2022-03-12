FROM gradle:7.4.1@sha256:0bff3b02ade9367dbcc6ee16714a01aa33b96bfbf77b9586f5ed9ad3585e0b6a AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:8599dadcfaabc56f89f6ef8fe619861792e0d6bd3274f1440fd557dcc2951cfc

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
