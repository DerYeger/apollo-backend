FROM gradle:7.1.0 AS BUILDER

WORKDIR /usr/app/

COPY . .

RUN gradle shadowJar

FROM adoptopenjdk/openjdk14:alpine

COPY --from=BUILDER /usr/app/build/libs/apollo.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo.jar" \
]
