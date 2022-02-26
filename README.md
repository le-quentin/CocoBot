# CocoBot
A Discord bot watching people chat and then able to impersonate them

## Installation

You will need java 15 installed.

Setup the bot token in the config file: 
- Copy `resources/config/secrets.yaml.template` into `resources/config/secrets.yaml`
- Set your bot token value in that new file

Check that everything is alright by running tests:

```shell
./gradlew test
```

## Run

```shell
./gradlew run
```