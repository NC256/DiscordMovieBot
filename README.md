# DiscordMovieBot
A quick and dirty Discord bot that I wrote to tally movie ratings for my friends on Discord.

## Explanation
This bot takes in all the messages in a given channel and ranks them by average rating which is determined by counting the emoji reactions.
Currently the emojis 0 through 10 are used, along with the pause button which is rated as an 11, and the clock with hands facing up which 
is rated as 12. It's currently a 0 through 12 scale.

### Commands
Currently the following commands are implemented:

- `!rankings channel_name` Returns an ordered list of all movies in descending order based on average rating.
- `!myRankings channel_name [username]` Returns an ordered list of all movies that the given user has rated. Defaults to the user who sent the command if no username is provided.
- `!checkCompletion channel_name [username]` Returns a list of movies that the user has not rated or has rated twice accidentally. Defaults to the user who sent the command if no username is provided.
- `!opinions channel_name movie_name` Returns an ordered list of all ratings for the given movie_name.
- `!randomMovie channel_name` Returns a random movie from the given channel that has been reacted to with a red dot (used to indicate not having watched that movie yet).
- `!secretSanta [comma separated name list]` Shuffles all the names in the list so that a secret santa gift exchange can be done.

## Built With

* [JDA - (Java Discord API)](https://github.com/DV8FromTheWorld/JDA) - A Java API for Discord's REST api and Websocket-Events.
