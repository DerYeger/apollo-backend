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
    <img alt="Docker" src="https://img.shields.io/docker/cloud/build/deryeger/apollo-backend?color=%2300C460&label=Docker%20Hub&logo=docker">
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
- ☑️ **Assignments**: Multiple assignments, which build an understanding for first-order logic and model checking
- 💹 **Feedback**: Three different feedback levels provide insight on model-checking results and assignment solutions

## Links

- [Apollo](https://github.com/DerYeger/apollo)
- [Apollo-Frontend](https://github.com/DerYeger/apollo-frontend)
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

### Documentation

Run the Gradle `dokkaHtml` task to generate the documentation. It will be stored in the `build/dokka/html/` directory.

### Docker

Run `docker-compose up -d --build` to build and start a container. Alternatively, build the image via the Dockerfile.
>Note: Default port is 8080.

## Licenses

[BSD 3-Clause License](./LICENSE) - Copyright &copy; Jan Müller

The parsing and validation logic is built upon a Java desktop application by Arno Ehle and Benedikt Hruschka.
The following license precedes the affected code.

[BSD 3-Clause License](./src/eu/yeger/apollo/fol/LICENSE) - Copyright &copy; Arno Ehle, Benedikt Hruschka
