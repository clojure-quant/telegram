# telegram [![GitHub Actions status |clojure-quant/telegram](https://github.com/clojure-quant/telegram/workflows/CI/badge.svg)](https://github.com/clojure-quant/telegram/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/io.github.clojure-quant/telegram.svg)](https://clojars.org/io.github.clojure-quant/telegram)


This library is very optinionated and allows to easily add a telegram bot to an application.
It has two features
- "Human RPC" interface: user can execute a command (with args parameter)
- "Publish/Subscribe: user can subscribe to a topic and bot can publish topics


# demo

First you need to create a bot token: in telegram app talk to: BotFather and create a new telegram bot.

In demo/src/config-template.edn enter your telegram bot token and name.
Then rename the file to demo/src/config.edn (so remove -template)

then run:

  cd demo
  clj -X:demo

You will see a list of commands in a chat with the bot, and can interact with it. Every 5 minutes a topic with random data is published which will be
showing in the chat (after subscribing)

To set the icon of the bot talk to the BotFather and upload an image.





