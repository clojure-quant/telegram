# telegram

This library is very optinionated and allows to easily add a telegram bot to an application.
It has two features
- "Human RPC" interface: user can execute a command (with args parameter)
- "Publish/Subscribe: user can subscribe to a topic and bot can publish topics


# demo

First you need to create a bot token: in telegram app talk to: BotFather and create a new telegram bot.

In demo/resources/config-demo.edn enter your telegram bot token and name.
Then rename the file to demo/resources/config.edn

then run:

  cd demo
  clj -X:demo

You will see a list of commands in a chat with the bot, and can interact with it. Every 5 minutes a topic with random data is published which will be
showing in the chat (after subscribing)







