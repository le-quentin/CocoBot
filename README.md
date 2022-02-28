# CocoBot
A Discord bot watching people chat and then able to impersonate them

## TODO

### Core
- [ ] synchronise task should add to existing DB (store lastSyncDate)
- [ ] Then coco could sync at startup, syncAll if date null
- [ ] Keep testing with existing impersonators to produce funnier outputs

### Ergonomy
- [ ] c/help
- [ ] rewrite conf from env properly
- [ ] prefix in conf

### Deploy
- [ ] Dockerize: Dockerfile to put the jar in a jdk container
- [ ] Registry: use github package to get the image, and run in prod without sources

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