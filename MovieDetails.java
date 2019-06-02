import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDetails implements Command {
    //!movieDetails #channelName #movieName

    MessageReceivedEvent message;
    String[] args;

    MovieDetails(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }


    @Override
    public String execute() {

        Guild thisGuild = message.getGuild();

        //StringBuilder for crafting the return message
        //String[] input for splitting each word in the command
        StringBuilder returnString = new StringBuilder();
        String channelName;
        String movieName = null;
        Message mainMovie = null;

        //If incorrectly entered, return error, otherwise mark down the channel name
        channelName = args[0];


        //Movie names can have spaces in them, so the name needs to be reassembled
        if (args.length == 2) {
            movieName = args[1];
        } else {
            movieName = MyUtils.rebuildUsername(args, 1);
        }

        if (movieName == null) {
            return "No movie name provided";
        }

        //Getting reference to the channel with all the movies in it
        TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, thisGuild);
        if (movieChannel == null) {
            return "Cannot find that channel";
        }

        //Get all movies from that channel
        List<Message> movies = MyUtils.getValidMoviesFromTextChannel(movieChannel);

        //Look through the list for our movie
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getContentDisplay().toLowerCase().startsWith(movieName.toLowerCase())) {
                mainMovie = movies.get(i);
            }
        }
        if (mainMovie == null) {
            return "Could not find that movie.";
        }


        //Append movie name to top of return message
        returnString.append(mainMovie.getContentDisplay());
        returnString.append("'s ratings:");
        returnString.append("\n");

        //Get all reactions from the movie we've found
        List<MessageReaction> oldReactions = mainMovie.getReactions();
        ArrayList<MessageReaction> reactions = new ArrayList<>();


        //Copy all reactions into an ArrayList, due to mainMovie.getReactions() returning
        // an immutable list
        for (int i = 0; i < oldReactions.size(); i++) {
            reactions.add(oldReactions.get(i));
        }

        //Remove all invalid MessageReactions
        reactions.removeIf(i -> !MyUtils.isValidReaction(i.getReactionEmote().getName()));

        //While my list of reactions isn't empty
        while (!reactions.isEmpty()) {

            int highest = -1;
            int highestIndex = -1;

            //This loop gets the highest value reaction in the list
            for (int i = 0; i < reactions.size(); i++) {
                int currentReactionValue = MyUtils.getValidReactionValue(reactions.get(i).getReactionEmote().getName());
                //if it's higher than the highest so far
                if (currentReactionValue > highest) {
                    highest = currentReactionValue;
                    highestIndex = i;
                }
            }

            //Name (emoji as string)
            String highestReactionName = reactions.get(highestIndex).getReactionEmote().getName();

            //Use this garbage to get the users for the current reaction
            Object[] objectArray = reactions.get(highestIndex).getUsers().stream().toArray();

            //Only need usernames, so turning that into an array so we can alphabetize them
            String[] userNameArray = new String[objectArray.length];


            //Moving them all over, requires casting back to a User object first
            for (int i = 0; i < objectArray.length; i++) {
                User temp = (User) objectArray[i];
                userNameArray[i] = temp.getName();
            }

            //Alphabetize names
            Arrays.sort(userNameArray);

            //Append the highest rated emoji
            returnString.append(highestReactionName);
            returnString.append(":");
            returnString.append("\n");

            //For the list of users on this reaction, append them
            for (int k = 0; k < objectArray.length; k++) {
                returnString.append("-       ");
                returnString.append(userNameArray[k]);
                returnString.append("\n");
            }
            //Remove processed emoji from list of reactions
            reactions.remove(highestIndex);
        }
        return returnString.toString();
    }
}
