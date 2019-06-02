import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class MovieCompletion implements Command {
    //!movieCompletion #channelName #optionalUsername

    MessageReceivedEvent message;
    String[] args;

    MovieCompletion(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }


    //This class checks for the following:
    //Rating twice
    //Not rated

    @Override
    public String execute() {
        TextChannel thisChannel = message.getTextChannel();
        double startTime = System.currentTimeMillis();
        Guild thisGuild = message.getGuild();
        User thisUser = message.getAuthor();
        StringBuilder returnString = new StringBuilder();
        String username = null;
        String channelName;

        channelName = args[0];


        if (args.length == 2) {
            username = args[1];
        } else if (args.length > 2) {
            username = MyUtils.rebuildUsername(args, 1);
        }

        TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, thisGuild);

        if (movieChannel == null) {
            return "Cannot find that channel";
        }
        List<Message> movies = MyUtils.getValidMoviesFromTextChannel(movieChannel);

        //Array to count how many ratings per movie the user has made
        int[] numOfRatings = new int[movies.size()];

        //If no username provided, assume user who sent message
        if (username == null) {
            returnString.append(thisUser.getName());
            returnString.append("'s Movie Completion:\n");
        } else {
            thisUser = MyUtils.getUserByName(username, thisGuild);
            if (thisUser == null) {
                return "Person not found or multiple users with that name.";
            } else {
                returnString.append(thisUser.getName());
                returnString.append("'s Movie Completion:\n");
            }
        }

        //Loop is very slow, so heads up message is sent
        thisChannel.sendMessage("Working...this could take a few seconds...").queue();

        //For each movie
        for (int i = 0; i < movies.size(); i++) {

            //Get a list of all reactions
            List<MessageReaction> reactions = movies.get(i).getReactions();

            //For each reaction to the movie
            for (int k = 0; k < reactions.size(); k++) {

                //Get its name
                String currentReactionName = reactions.get(k).getReactionEmote().getName();

                //If it isn't a valid reaction, move to the next one
                if (!MyUtils.isValidReaction(currentReactionName)) {
                    continue;
                }

                //If we find the user in any given reaction, we count that as a rating
                final User workAround = thisUser;
                if (reactions.get(k).getUsers().stream().anyMatch(s -> s.equals(workAround))) {
                    numOfRatings[i]++;
                }
            }
        }

        //We've counted the number of ratings per movie, if it's 0 then they haven't
        // rated it, and if it's 2+ then they've rated more than once
        for (int i = 0; i < numOfRatings.length; i++) {
            if (numOfRatings[i] == 0) {
                returnString.append("You have not rated ");
                returnString.append(movies.get(i).getContentDisplay());
                returnString.append("\n");
            } else if (numOfRatings[i] >= 2) {
                returnString.append("You have rated ");
                returnString.append(movies.get(i).getContentDisplay());
                returnString.append(" more than once!\n");
            }
        }
        double endTime = System.currentTimeMillis();
        returnString.append("That took ");
        returnString.append(endTime - startTime);
        returnString.append(" milliseconds\n");
        return returnString.toString();
    }

}
