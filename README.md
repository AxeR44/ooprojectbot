# OOProjectBot
This is a discord bot developed in java using JDA and deployed using heroku

## Discord Bot Commands
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
.play <query>: searches for a song on youtube and plays the first result
.skip: skips a track
.queue: displays the track queue
.dequeue <index>: deletes the track at index <index> from the queue
.seek <time>: seeks through a media content
.leave: disconnects from a voice channel
.translate <text> -- <sourceLanguage> <targetLanguage>: Translates <text> from <sourceLanguage> to <targetLanguage>
.langlist: lists all the languages supported by the .translate command
.votekick @<username>: kicks a user from the server
.survey <question> -- YesNo: creates a simple survey (Yes/No)
.survey <question> -- custom -- [emotes]: creates a custom survey with custom answers 
.endSurvey <surveyID>: ends a survey displaying the result
.coinToss: flips a coin
.report @<user>: reports a user to the server owner 
.softban @<user> -- <reason> -- <timeinSeconds>: softbans a user
.wiki <query>: Searches a page on wikipedia
.lyrics: Displays lyrics for playing song
.lyrics <query>: Searches for lyrics.
.reminder <object> -- <timeinSeconds>: remindes a user of something
.roll <nDice>d<nFaces>: rolls dices
```

## Telegram Bot Commands:
The Telegram bot can handle the following commands:

```
/sendDiscord <message> -- <guildName>: sends a message to the main text channel of a guild
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
* [x] Making discord -> telegram interaction bidirectional
* [x] Report user
* [x] Searching content on Wikipedia
* [x] Softban
* [x] Searching lyrics
* [x] Reminder 
* [x] Searching songs without link (.play <query>)
* [x] Asynchronous Discord message handling
* [x] Seeking
* [x] Dice roll
* [x] Can add/remove Role with reaction on a message


## Authors
* **Alessandro Albini** - [AxeR44](https://github.com/AxeR44)
* **Alessandro Soraci** - [Sandrone99](https://github.com/Sandrone99)


