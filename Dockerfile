FROM gradle:7.1.1@sha256:19d9bdf24a291e5d7ac758c8d0c7d8f2f5d641b130d17b556dbab2c49701bf3e AS BUILDER

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
