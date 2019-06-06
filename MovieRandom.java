import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;

/**
 * This movie randomly returns the name of a movie that is marked as unwatched
 */
public class MovieRandom implements Command {

    MessageReceivedEvent message;
    String[] args;

    MovieRandom(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    public String execute() {
        StringBuilder stringBuilder = new StringBuilder();

        TextChannel movieChannel = MyUtils.getTextChannelByName(args[0], message.getGuild());
        if (movieChannel == null) {
            return "Cannot find that channel";
        }

        List<Message> movies = MyUtils.getRedDotMovies(movieChannel);
        if (movies.size() == 0) {
            return "You've watched all the movies!";
        }

        Random myRand = new Random();

        int random = myRand.nextInt(movies.size());
        stringBuilder.append(movies.get(random).getContentDisplay());

        return stringBuilder.toString();
    }
}