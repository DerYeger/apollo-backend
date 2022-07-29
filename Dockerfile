FROM gradle:7.5.0@sha256:b832c8452c1424a81b770400eadca0fa463db1eef19f7c403bf9e8b6288f93b4 AS BUILDER

WORKDIR /app/

# Copy dependency-related files
COPY build.gradle.kts gradle.properties settings.gradle.kts /app/

# Only download dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

# Copy all files
COPY ./ /app/

# Build jar
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk16:alpine@sha256:b22a2993bc794873965c2ed0cc76d0305a9a45a280a43c27937723c7fc68c356

COPY --from=BUILDER /app/build/libs/apollo-backend.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo-backend.jar" \
]
