FROM gradle:7.4.0@sha256:5248d0e8f7f6ad2095c3a053d5461daa17b02097410f0e9f6397f8f4dedc34bf AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:59d8cf0fa31e07e5a66faf8d8941ee39a7b519f85b575b928b7506d44fa28bb5

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
