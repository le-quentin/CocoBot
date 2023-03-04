# CocoBot
A Discord bot watching people chat and then able to impersonate them

## Generate your server's message file

Currently, the bot needs your servers' message in a plain json file. It doesn't generate it on startup, and doesn't update it.

To generate that file, you can run the synchronise task. It will need your BOT_TOKEN:

```shell
> BOT_TOKEN =<TOKEN> ./gradlew synchronise`
```

/!\ This will leave your token in your bash history. If you feel iffy about that (as you should) then either put it in your `.bashrc` file, or just run:

```shell
> ./gradlew synchronise
```

and input your token when prompted. 

## Run with docker

Your need to put `messages.json` file in a dedicated folder, then run the docker container like this: 

```shell
> docker run -e BOT_TOKEN=<token> -v /your/dedicated/folder:/app/data:ro ghcr.io/le-quentin/cocobot:latest
```

Again, your token should probably be set in your shell's startup files to avoid putting it your cli history.

## Run with gradle

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
- [x] messages.json outside of docker image (not necessary if using Mongo or Protobuf)
- [ ] alternative images for armv7 and amd64*
- [ ] GitHub actions to automatically push image on commit push

### Release as a public bot
- [ ] Multiple servers setup => store message in separate documents/files for each server, with impersonators local to them
- [ ] Support english language
- [ ] Ways to dynamically setup some stuf like prefix and language
- [ ] Test a way to erase one user's data from all documents (GDPR)
- [ ] Monitor/logging tools should be improved

