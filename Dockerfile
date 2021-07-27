FROM gradle:7.1.0@sha256:92c0f3381fd8db612dac10b5b584d68376c192bae6b1a11b2190780a60411fe4 AS BUILDER

WORKDIR /usr/app/

COPY . .

RUN gradle shadowJar

FROM adoptopenjdk/openjdk14:alpine@sha256:60b78adc0f23c207d5f60a254adf62f4ae420156e42e514a16791d73581134f1

COPY --from=BUILDER /usr/app/build/libs/apollo.jar .

ENTRYPOINT [ \
    "java", \
    "-server", \
    "-jar", \
    "apollo.jar" \
]
