![image](https://user-images.githubusercontent.com/6195955/222922200-45035f29-aaf9-4738-92e5-e9ce6313c687.png)


# <p align=center>CocoBot</p>

<p align=center>Learns from your friends' messages, and impersonates them. Because nonsense is fun.</p>
<p align=center>
<a href="https://github.com/le-quentin/CocoBot/actions/workflows/test-and-build.yaml"><img src="https://img.shields.io/github/actions/workflow/status/le-quentin/CocoBot/test-and-build.yaml"/></a>
<a href="https://github.com/le-quentin/CocoBot/releases"><img src="https://img.shields.io/github/v/release/le-quentin/Cocobot"/></a>
</p>
<p align=center>
<a href="https://github.com/le-quentin/CocoBot/blob/master/LICENSE"><img src="https://img.shields.io/github/license/le-quentin/cocobot"/></a>
<img src="https://img.shields.io/badge/Arch-AMD%20%F0%9F%92%BB-yellow"/> 
<img src="https://img.shields.io/badge/Arch-ARM%F0%9F%8D%87-yellow"/> 
<img src="https://img.shields.io/tokei/lines/github/le-quentin/CocoBot"/>
</p>

## I just wanna run it quick!

Gotcha! Create [a bot application](https://discord.com/developers/docs/getting-started#creating-an-app) in your Discord server, and tick "Message content intent" in the bot settings. Then add the bot to your server with those permissions:
- View channels
- Send messages
- Read messages history

You're almost there! Run the bot with:

```shell
> docker run -e BOT_TOKEN=<token> ghcr.io/le-quentin/cocobot:latest
```

...give it a few minutes for the initial scraping of messages...

And type `c/me` or `c/like <username>` to make Coco do funny impersonations!

See below for more details. :) Have fun!

## What is this?
A Discord bot watching people chat and then able to "impersonate" them. It will use all of the impersonated person
messages in order to produce a random message based on them. The message won't always be gramatically correct, but it is 100% guaranteed to be wacky and somewhat funny.
We've been using it with my friends for a while, and we had a few laughs here and there. 

I don't host a public version of this bot (yet?), so you have to host it yourself. That being said, I offer up-to-date docker images via `github packages`, so you don't have
to build it, you just need a server with docker installed!

Also, feel free to modify/extend the source code, depending on your needs and liking. The code makes heavy use of decorators and adapters, so it's a little messy, but very easy to extend (for example, porting the bot to another chat than Discord shouldn't be too much trouble). Feel free to fork and/or drop me a PR! 

**⚠️WARNING⚠️**: Currently, this bot is designed to handle only one server. You can invite it to more than one server, but be warned that everyone will be able to impersonate everyone with no server borders whatsoever... I recommend you stick to only one server per bot instance if you're afraid it might cause awkward situations. :)

## Commands 

From any channel where the bot is invited:
```
c/me                - the bot impersonates you
c/like <username>   - the bot impersonates that user
```

`<username>` should be the actual username (not the server local nickname), without the `#<digits>` part. So to impersonate `JohnDoe#1234`, do:
```
c/like JohnDoe
c/like johndoe
```

Both work, it's not case sensitive.

## Host the bot

### Discord setup 

Go to https://discord.com/developers/applications and create an application for the bot. Go in the `Bot` section and create a bot for that application. 

The bot will have a secret token: note it down, you'll need it later.

Scroll down and tick `Message content intent`, it needs to be on.

Then, invite the bot on your server (either with an oauth link, or simply by using its username). The required permissions are: 
- View channels
- Send messages
- Read messages history

...and that's it!

### Run the bot with docker 

Running the bot is as easy as: 
```shell
> docker run -e BOT_TOKEN=<token> ghcr.io/le-quentin/cocobot:latest
```

...with `<token>` obviously being your bot secret token (I recommend using an env var set in your shell startup files, to avoid printing the secret in your shell's history). For a list of env vars for bot configuration, see [bot configuration section](#configuration)

`latest` is the last stable release. If you want a specific version, checkout [releases](https://github.com/le-quentin/CocoBot/releases), every release has a matching docker image tag.

At the first container's startup, the bot will parse all the server's messages, which will take a while (~5 minutes for my server, could be way longer on a huge community server). Currently, the bot does not update this file after the first start: if you want to get all the messages again, recreate the container.

If you would like to be able to recreate the container (to get updated images, typically) without having to regenerate all messages every time, you can use a docker volume. Create a directory dedicated to storing the messages. Then:

```shell
> docker run -e BOT_TOKEN=<token> -v /path/to/dedicated/dir:/app/data ghcr.io/le-quentin/cocobot:latest
```

This way, any version of the bot will start using the messages stored in `/path/to/dedicated/dir/messages.json` (and the bot will generate the file on first run, as usual). If you want to get all messages again, simpy delete the file and restart the container.

## Build it yourself

If you don't want to use docker, or if you'd like to extend/modify the bot, you need to build it yourself.

Thankfully, gradle wrapper makes it all too easy. Clone the repository, then from the root directory, simply run:

```shell
> ./gradlew build
```

...to build the service (it will also run tests), and:

```shell
> BOT_TOKEN=<token> ./gradlew run
```

...to run it. You will need to create a `data` folder under your current directory first.

Gradle wrapper should take care of everything, including downloading the appropriate JDK. You literally should have nothing else to do.

## Configuration

You can change the bot configuration with env vars. Here's the list of available vars:

```
BOT_TOKEN (required) - your bot's secret token
LANGUAGE             - the bot's language, using the 2 chars ISO code. Values: en,fr. Default: en.
```

## TODO list - things I might change/add

### Core
- [x] Exclude quotes from parsed messages*
- [x] Sync at startup if messages file not found*
- [ ] Move to MongoDB or ProtoBuf for better performance
- [ ] Keep testing with existing impersonators to produce funnier outputs

### Ergonomy
- [ ] c/help
- [x] rewrite conf from env properly
- [ ] prefix in conf
- [ ] Proper logging with timestamp and levels

### Deploy | CI/CD
- [x] messages.json outside of docker image (not necessary if using Mongo or Protobuf)
- [x] use platform image param to publish amd64/armv7/arm64
- [x] GitHub actions to automatically push image on commit push
- [x] Test workflow on feature branch push
- [x] Build `nightly` docker image on `master` push => protect master against push (work with merges from now on)

### Release as a public bot
- [ ] Multiple servers setup => store message in separate documents/files for each server, with impersonators local to them
- [x] Support english language
- [ ] Ways to dynamically setup some stuff like prefix and language
- [ ] Test a way to erase one user's data from all documents (GDPR)
- [ ] Monitor/logging tools should be improved

