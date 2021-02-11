# gramoFO-Backend

> A web application for first-order model checking in graph structures

Documentation is available [here](https://gramofo-backend.yeger.eu/gramofo).

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
