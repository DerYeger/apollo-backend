<h1 align="center">gramoFO-Backend</h1>

<p align="center">
  <img src="https://raw.githubusercontent.com/DerYeger/gramofo-frontend/master/src/assets/icons/android-chrome-512x512.png" alt="Logo" width="128" height="128">
</p>

<p align="center">
  <a href="https://github.com/DerYeger/gramofo-backend/actions/workflows/ci.yml">
    <img alt="CI" src="https://github.com/DerYeger/gramofo-backend/actions/workflows/ci.yml/badge.svg?event=push">
  </a>
  <a href="https://github.com/DerYeger/gramofo-backend/actions/workflows/cd.yml">
    <img alt="CD" src="https://github.com/DerYeger/gramofo-backend/actions/workflows/cd.yml/badge.svg">
  </a>
  <a href="https://github.com/DerYeger/gramofo-backend/actions/workflows/maintenance.yml">
    <img alt="Maintenance" src="https://github.com/DerYeger/gramofo-backend/actions/workflows/maintenance.yml/badge.svg">
  </a>
</p>

<p align="center">
   <a href="https://gramofo.yeger.eu/">
    gramofo.yeger.eu
  </a>
</p>

> A web application for first-order model checking in graph structures

## Features

- ✨ **Model Checking**: First-order-logic model checking in graph structures
- 💹 **Feedback**: Three different feedback levels provide insight on model-checking results

## Links

- [gramoFO](https://github.com/DerYeger/gramofo)
- [gramoFO-Frontend](https://github.com/DerYeger/gramofo-frontend)
- [Documentation](https://gramofo-backend.yeger.eu/)

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

## License

[BSD 3-Clause License](./LICENSE) - Copyright &copy; Jan Müller
