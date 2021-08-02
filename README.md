<h1 align="center">Apollo-Backend</h1>

<p align="center">
  <img src="https://raw.githubusercontent.com/DerYeger/apollo-frontend/master/src/assets/icons/android-chrome-512x512.png" alt="Logo" width="128" height="128">
</p>

<p align="center">
  <a href="https://github.com/DerYeger/apollo-backend/actions/workflows/ci.yml">
    <img alt="CI" src="https://github.com/DerYeger/apollo-backend/actions/workflows/ci.yml/badge.svg?event=push">
  </a>
  <a href="https://github.com/DerYeger/apollo-backend/actions/workflows/cd.yml">
    <img alt="CD" src="https://github.com/DerYeger/apollo-backend/actions/workflows/cd.yml/badge.svg">
  </a>
  <a href="https://github.com/DerYeger/apollo-backend/actions/workflows/maintenance.yml">
    <img alt="Maintenance" src="https://github.com/DerYeger/apollo-backend/actions/workflows/maintenance.yml/badge.svg">
  </a>
</p>
<p align="center">
  <a href="https://hub.docker.com/repository/docker/deryeger/apollo-backend">
    <img alt="Docker Image Version (latest semver)" src="https://img.shields.io/docker/v/deryeger/apollo-backend?logo=docker&sort=semver">
  </a>
</p>

<p align="center">
   <a href="https://apollo.yeger.eu/">
    apollo.yeger.eu
  </a>
</p>

> A web application for first-order model checking in graph structures.

This project and the accompanying bachelor's thesis were inspired by the research group *Theoretical Computer Science / Formal Methods* by the University of Kassel.
The parsing and validation logic of the backend is built upon a Java desktop application by Arno Ehle and Benedikt Hruschka.

> Note: This project is also known as **gramoFO**.

## Features

- ✨ **Model Checking**: First-order-logic model checking in graph structures
- ☑️ **Assignments**: Various assignments, which build an understanding for first-order logic and model checking
- 💹 **Feedback**: Three different feedback levels provide insight on model-checking results and assignment solutions

## Links

- [Apollo](https://github.com/DerYeger/apollo)
- [Apollo-Frontend](https://github.com/DerYeger/apollo-frontend)
- [Apollo-Admin](https://github.com/DerYeger/apollo-admin)
- [Documentation](https://apollo-backend.yeger.eu/)
- [Bachelor's thesis (German)](https://jan-mueller.at/documents/bachelor-thesis)
- [Docker](https://hub.docker.com/repository/docker/deryeger/apollo-backend)

## Development

### Installation

Importing or reloading the Gradle project will also install all required dependencies.

### Development server

The Gradle `run` task will start the development server.

>Note: Default port is 8080.

### Linting & formatting

Run the Gradle `ktlintFormat` task to lint and format all source files. It will automatically run before compilation.

## Build

Run the Gradle `build` task to generate a `.jar` file. The build artifacts will be stored in the `build/libs/` directory.

### Running tests

Run the Gradle `test` task to start the development server.

## Deployment

Deployment via Docker is highly recommended.
The following configuration is a baseline.

> Please note that manual creation of the `*.secret` files, found at the of the configuration, is required.
> They must contain a single line.

```yaml
version: "3.7"

services:
  # Apollo Backend
  apollo-backend:
    container_name: apollo-backend
    image: deryeger/apollo-backend:v2.5.0
    ports:
      - "8080:8080"
    networks:
      - apollo-network
    secrets:
      - database_user
      - database_password
      - default_username
      - default_password
      - jwt_secret
    depends_on:
      - postgres
    environment:
      DATABASE_HOST: "postgres"
      DATABASE_PORT: "5432"
      DATABASE_NAME: "apollo-database"
    restart: unless-stopped
  # SQL database
  postgres:
    image: postgres:13.3@sha256:6647385dd9ae11aa2216bf55c54d126b0a85637b3cf4039ef24e3234113588e3
    container_name: apollo-postgres
    ports:
      - "5432:5432"
    volumes:
      - "apollo-data:/var/lib/postgresql/data"
    networks:
      - apollo-network
    secrets:
      - database_user
      - database_password
    environment:
      POSTGRES_USER_FILE: "/run/secrets/database_user"
      POSTGRES_PASSWORD_FILE: "/run/secrets/database_password"
      POSTGRES_DB: "apollo-database"
    restart: unless-stopped

volumes:
  apollo-data:

networks:
  apollo-network:

secrets:
  database_user:
    file: ./secrets/database_user.secret
  database_password:
    file: ./secrets/database_password.secret
  default_username:
    file: ./secrets/default_username.secret
  default_password:
    file: ./secrets/default_password.secret
  jwt_secret:
    file: ./secrets/jwt_secret.secret
```

## Licenses

[BSD 3-Clause License](./LICENSE) - Copyright &copy; Jan Müller
