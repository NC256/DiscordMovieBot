import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns the rankings of a single individual across all movies in a given channel
 */
public class MoviePersonalRankings implements Command {


    MessageReceivedEvent message;
    String[] args;

    MoviePersonalRankings(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    @Override
    public String execute() {

        TextChannel movieChannel = MyUtils.getTextChannelByName(args[0], message.getGuild());
        if (movieChannel == null) {
            return "Cannot find that channel.";
        }

        final User thisUser = MyUtils.getUserByInput(args, message.getAuthor(), message.getGuild());
        if (thisUser == null) {
            return "Person not found or multiple users with that name.";
        }

        List<Message> validMovies = MyUtils.getValidMovies(movieChannel);
        if (validMovies.size() == 0) {
            return "No movies found in that channel.";
        }

        ArrayList<Integer> movieRatings = new ArrayList<>();
        ArrayList<String> movieNames = new ArrayList<>();

        message.getChannel().sendMessage("Working...this could take a few seconds...").queue();

        for (Message m : validMovies) {

            int userRating = MyUtils.getUserRatingFromMovie(m, thisUser);

            if (userRating != -1) {
                movieRatings.add(userRating);
                movieNames.add(m.getContentDisplay());
            }
        }
        StringBuilder stringBuilder = new StringBuilder();

        //Discord formatting
        stringBuilder.append("```");

        stringBuilder.append(thisUser.getName()).append("'s Movie Ratings:\n");

        while (!movieRatings.isEmpty()) {
            int highestRatingFound = -1;
            int indexOfHighest = -1;

            for (int i = 0; i < movieRatings.size(); i++) {
                if (movieRatings.get(i) > highestRatingFound) {
                    highestRatingFound = movieRatings.get(i);
                    indexOfHighest = i;
                }
            }
            stringBuilder.append(String.format("%2d: %s\n", movieRatings.get(indexOfHighest), movieNames.get(indexOfHighest)));
            movieNames.remove(indexOfHighest);
            movieRatings.remove(indexOfHighest);
        }
        stringBuilder.append("```");
        return stringBuilder.toString();
    }
}

