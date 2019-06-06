import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class MovieDetails implements Command {

    MessageReceivedEvent message;
    String[] args;

    MovieDetails(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    @Override
    public String execute() {

        TextChannel movieChannel = MyUtils.getTextChannelByName(args[0], message.getGuild());
        if (movieChannel == null) {
            return "Cannot find that channel";
        }

        String movieName;
        if (args.length == 2) {
            movieName = args[1];
        }
        //Movie names can have spaces in them, so the name needs to be reassembled
        else {
            movieName = MyUtils.rebuildUsername(args, 1);
        }
        if (movieName == null) {
            return "No movie name provided";
        }

        List<Message> validMovies = MyUtils.getValidMovies(movieChannel);
        if (validMovies.size() == 0) {
            return "No movies found in that channel.";
        }

        Message movieMessage = null;
        //Look through the list for our movie
        for (Message m : validMovies) {
            if (m.getContentDisplay().toLowerCase().startsWith(movieName.toLowerCase())) {
                movieMessage = m;
            }
        }
        if (movieMessage == null) {
            return "Could not find that movie.";
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(movieMessage.getContentDisplay());
        stringBuilder.append("'s ratings:\n");

        //Create a mutable copy of the reactions on the Message to manipulate
        List<MessageReaction> movieReactions = new ArrayList<>(movieMessage.getReactions());

        //Remove all invalid MessageReactions
        movieReactions.removeIf(i -> !MyUtils.isValidReaction(i.getReactionEmote().getName()));

        while (!movieReactions.isEmpty()) {

            int highestRatingFound = -1;
            int indexOfHighest = -1;

            for (int i = 0; i < movieReactions.size(); i++) {
                int currentReactionValue = MyUtils.getValidReactionValue(movieReactions.get(i).getReactionEmote().getName());

                if (currentReactionValue > highestRatingFound) {
                    highestRatingFound = currentReactionValue;
                    indexOfHighest = i;
                }
            }

            //Get array of all users who have reacted to the movie
            User[] reactedUsers = movieReactions.get(indexOfHighest).getUsers().stream().toArray(User[]::new);

            String reactionEmoji = movieReactions.get(indexOfHighest).getReactionEmote().getName();
            stringBuilder.append(reactionEmoji);
            stringBuilder.append(":\n");

            //For the list of users on this reaction, append their names
            for (User u : reactedUsers) {
                stringBuilder.append("-       ");
                stringBuilder.append(u.getName()).append("\n");
            }
            movieReactions.remove(indexOfHighest);
        }
        return stringBuilder.toString();
    }
}