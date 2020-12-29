FROM gradle:6.7.1 AS BUILDER

WORKDIR /usr/app/

COPY . .

RUN gradle shadowJar

FROM adoptopenjdk/openjdk14:alpine

COPY --from=BUILDER /usr/app/build/libs/gramofo.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "gramofo.jar" \
]