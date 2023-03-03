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

## TODO

* means required before open sourcing

### Core
- [x] Exclude quotes from parsed messages
- [ ] synchronise task should add to existing DB (store lastSyncDate)*
- [ ] Then coco could sync at startup, syncAll if date null*
- [ ] Move to MongoDB or ProtoBuf for better performance
- [ ] Keep testing with existing impersonators to produce funnier outputs

### Ergonomy
- [ ] c/help*
- [ ] rewrite conf from env properly
- [ ] prefix in conf*

### Deploy
- [ ] Proper logging with timestamp and levels
- [ ] stored_messages outside of docker image (not necessary if using Mongo or Protobuf)
- [ ] alternative images for armv7 and amd64*
- [ ] GitHub actions to automatically push image on commit push

### Release as a public bot
- [ ] Multiple servers setup => store message in separate documents/files for each server, with impersonators local to them
- [ ] Support english language
- [ ] Ways to dynamically setup some stuf like prefix and language
- [ ] Test a way to erase one user's data from all documents (GDPR)
- [ ] Monitor/logging tools should be improved

