# CocoBot
A Discord bot watching people chat and then able to impersonate them

## Installation

The project uses Gradle wrapper. It will take care of everything, even downloading the JDK version required for the project (namely 17).

Check that everything is alright by running tests:

```shell
./gradlew test
```

## Run

You need the BOT_TOKEN env var:

```shell
BOT_TOKEN=<your_token_here> ./gradlew run
```