# OOProjectBot
This is a discord bot developed in java using JDA and deployed using heroku

## Commands
Right now the bot can handle the following commands:

```
.help: displays the list of commands supported by OOProjectBot
.invite: displays the discord server invite link
.info: displays bot info
.telegram <message> -- <channelName>: sends a message to a telegram channel
.addTelegram <channelName> -- <chatID>: adds a new telegram channel to the list
.removeTelegram <channelName>: removes a telegram channel from the list
.listGroups: lists all the groups the bot can send messages to
.play <link>: connects to a voice channel and plays the audio track
.skip: skips a track
.queue: displays the track queue
.dequeue <index>: deletes the track at index <index> from the queue
.leave: disconnects from a voice channel
.translate <text> -- <sourceLanguage> <targetLanguage>: Translates <text> from <sourceLanguage> to <targetLanguage>
.langlist: lists all the languages supported by the .translate command
.votekick @<username>: kicks a user from the server
.survey <question> -- YesNo: creates a simple survey (Yes/No)
.survey <question> -- custom -- [emotes]: creates a custom survey with custom answers 
.endSurvey <surveyID>: ends a survey displaying the result
.coinToss: flips a coin
```

## TO-DO LIST

* [x] Sending Telegram message to a group
* [x] Displaying Telegram groups available for sending messages
* [x] Displaying bot info
* [x] Sending invite link to discord server
* [x] Displaying bot help
* [x] Playing songs
* [x] Skip songs
* [x] Queuing songs
* [x] Displaying songs queue
* [x] Translating text
* [x] Displaying available languages
* [x] Votekick user from server
* [x] Generating dynamic discord invite links
* [x] Creating simple surveys(yes/no)
* [x] Creating custom surveys with custom answers (using emotes)
* [x] Adding and removing Telegram groups
* [x] Every guild has its own Telegram Groups
* [x] Coin toss
* [ ] Making discord -> telegram interaction bidirectional
* [ ] Report user
* [ ] Searching content on Wikipedia
* [ ] Softban
* [ ] Searching songs without link (.play <songName>)


## Authors
* **Alessandro Albini** - [AxeR44](https://github.com/AxeR44)
* **Alessandro Soraci** - [Sandrone99](https://github.com/Sandrone99)


