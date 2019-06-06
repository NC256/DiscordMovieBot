import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * This class returns a formatted list from highest rated to lowest rated of all the movies in the channel specified
 */
public class MovieRankings implements Command {

    MessageReceivedEvent message;
    String[] args;

    MovieRankings(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    @Override
    public String execute() {
        StringBuilder stringBuilder = new StringBuilder();

        //Open code block for Discord formatting
        stringBuilder.append("```");

        TextChannel movieChannel = MyUtils.getTextChannelByName(args[0], message.getGuild());
        if (movieChannel == null) {
            return "Could not find that channel.";
        }

        List<Message> validMovies = MyUtils.getValidMovies(movieChannel);
        if (validMovies.size() == 0) {
            return "No valid movies found";
        }

        //Used for counting the while loop
        int movieCounter = 1;

        int numOfMovies = validMovies.size();

        //This grows at a rate of n(n+1)/2 (or O(n^2)), which is not great.
        //Pre-sorting this would likely make it run faster, but it runs fast enough for the time being
        while (!validMovies.isEmpty()) {

            double highestRatingFound = -1;
            int indexOfHighest = -1;

            for (int i = 0; i < validMovies.size(); i++) {

                double currentMovieRating = MyUtils.getAverageMovieRating(validMovies.get(i));

                if (currentMovieRating > highestRatingFound) {
                    highestRatingFound = currentMovieRating;
                    indexOfHighest = i;
                }
            }

            String movieName = validMovies.get(indexOfHighest).getContentDisplay();
            double movieRating = MyUtils.getAverageMovieRating(validMovies.get(indexOfHighest));

            int width = String.valueOf(numOfMovies).length();

            //Pads left by the width of the number of movies (1 space if < 10 movies , 2 if < 100, etc)
            //Right justifies the numbers, which keeps names and ratings aligned
            stringBuilder.append(String.format("%" + width + "s", movieCounter));

            stringBuilder.append(": ");

            //Limits to three decimal places for the rating and appends name
            stringBuilder.append(String.format("%6.3f - %s\n", movieRating, movieName));

            //Removes movie so we can search for the next highest
            validMovies.remove(indexOfHighest);
            movieCounter++;
        }
        //Close Discord code block
        stringBuilder.append("```");

        return stringBuilder.toString();
    }
}
