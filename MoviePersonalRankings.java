import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;


public class MoviePersonalRankings implements Command {
    //!myRankings #channelName #optionalUsername


    MessageReceivedEvent message;
    String[] args;

    MoviePersonalRankings(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    //Currently garbage
    //Takes 5-10 seconds to get un-sorted results back for a set of like ~23 messages
    //I need a better way to iterating over the data, but I don't know what that way is

    @Override
    public String execute() {
        double startTime = System.currentTimeMillis();

        Guild thisGuild = message.getGuild();
        User thisUser = message.getAuthor();
        TextChannel thisChannel = message.getTextChannel();

        StringBuilder returnString = new StringBuilder();
        returnString.append("```");
        String username = null;
        String channelName = args[0];

        //Because we split on spaces and usernames can contain spaces, we must treat
        // everything past the username input as one single username and reconstruct it
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

        //If no username provided, assume user who sent message
        if (username == null) {
            returnString.append(thisUser.getName());
            returnString.append("'s Movie Ratings:\n");
        } else {
            thisUser = MyUtils.getUserByName(username, thisGuild);
            if (thisUser == null) {
                return "Person not found or multiple users with that name.";
            } else {
                returnString.append(thisUser.getName());
                returnString.append("'s Movie Ratings:\n");
            }
        }

        double break1 = System.currentTimeMillis();
        returnString.append("Before Long Loop: ");
        returnString.append(break1 - startTime);
        returnString.append(" milliseconds\n");

        //One list contains the name as a String, the other contains the rating as an integer
        ArrayList<Integer> ratings = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        //Loop is very slow, so heads up message is sent
        thisChannel.sendMessage("Working...this could take a few seconds...").queue();

        //Adds movie name and rating from the user, assuming they gave it a rating
        for (Message m : movies) {

            //Get what the user rated the movie
            int userRating = MyUtils.getUserRatingFromMovie(m, thisUser);

            //If the user rated the movie
            if (userRating != -1) {
                ratings.add(userRating);
                names.add(m.getContentDisplay());
            }
        }

        double break2 = System.currentTimeMillis();
        returnString.append("After Long Loop: ");
        returnString.append(break2 - startTime);
        returnString.append(" milliseconds\n");

        //Sorting output
        while (!ratings.isEmpty()) {
            int highest = -1;
            int highestIndex = -1;

            for (int i = 0; i < ratings.size(); i++) {
                if (ratings.get(i) > highest) {
                    highest = ratings.get(i);
                    highestIndex = i;
                }
            }
            returnString.append(String.format("%2d: %-50s\n", ratings.get(highestIndex), names.get(highestIndex)));
            names.remove(highestIndex);
            ratings.remove(highestIndex);
        }
        double endTime = System.currentTimeMillis();
        returnString.append("That took ");
        returnString.append(endTime - startTime);
        returnString.append(" milliseconds\n");
        returnString.append("```");
        return returnString.toString();
    }
}

