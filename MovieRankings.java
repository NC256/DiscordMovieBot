import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class MovieRankings implements Command {
    //!rankings #channelName

    MessageReceivedEvent message;
    String[] args;

    MovieRankings(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    //This class is overly commented in order to help anyone who has no idea what they are looking at
    @Override
    public String execute() {
        try {

            Guild thisGuild = message.getGuild();

            //This StringBuilder will hold the text that contains the list of movies
            // and rankings. You can do this with a String, but String concatenation is
            // expensive and StringBuilders are designed for letting you add strings
            // together and are much faster
            StringBuilder returnString = new StringBuilder();

            //Open code block for Discord formatting
            returnString.append("```");

            //Gets the channel the user specifies from MyUtils class
            TextChannel movieChannel = MyUtils.getTextChannelByName(args[0], thisGuild);

            //If that method was unable to find the channel then it returns null, and we notify the user here
            if (movieChannel == null) {
                return "Could not find that channel.";
            }

            //We get a list of every message that is a valid movie
            List<Message> validMovies = MyUtils.getValidMoviesFromTextChannel(movieChannel);

            //If none of the messages in the channel are valid, then alert the user
            if (validMovies.size() == 0) {
                return "No valid movies found";
            }

            int counter = 1;

            //This grows at a rate of n(n+1)/2 (or O(n^2)), which is not great.
            //Pre-sorting this would likely make it run faster, but it runs fast enough for the time being
            //While my array of Movie objects isn't empty
            while (!validMovies.isEmpty()) {
                double highest = -1;
                int highestIndex = -1;

                //Loops over movies ArrayList until it finds the movie with the highest
                // average rating, which it then keeps track of that movie's score and
                // it's location in the ArrayList
                for (int i = 0; i < validMovies.size(); i++) {

                    //Gets the score of the movie
                    double currentAverage = MyUtils.getAverageMovieRating(validMovies.get(i));

                    //If the movie we're looking at has a higher rating then we track that one
                    if (currentAverage > highest) {
                        highest = currentAverage;
                        highestIndex = i;
                    }
                }

                //We get the name and rating and format it to look nice
                String movieName = validMovies.get(highestIndex).getContentDisplay();
                double movieRating = MyUtils.getAverageMovieRating(validMovies.get(highestIndex));
                returnString.append(String.format("%2s", counter));
                returnString.append(": ");
                returnString.append(String.format("%6.3f - %-50s\n", movieRating, movieName));
                //After we've done that, we remove this Movie from the ArrayList so we
                // can loop over it again looking for the next highest ranked movie
                validMovies.remove(highestIndex);
                counter++;
            }

            //Close code block
            returnString.append("```");

            //Finally we use the .toString() method of our StringBuilder to return the
            // results
            return returnString.toString();
        }

        //This usually happens when the user doesn't provide a channel name, in which
        // case our subString method call at the top that looks for the channel name
        // tries to look outside of the string we received, and throws this exception
        catch (IndexOutOfBoundsException e1) {
            return "No channel name provided or you need a space before the channel name";
        }
    }
}
