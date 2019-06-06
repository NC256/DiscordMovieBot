import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;


/**
 * This class returns a list of any movies you have not rated, or movies you have accidentally given two or more ratings
 */
public class MovieCompletion implements Command {

    MessageReceivedEvent message;
    String[] args;

    MovieCompletion(MessageReceivedEvent message, String[] args) {
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

        int[] numOfUserRatings = new int[validMovies.size()];

        message.getChannel().sendMessage("Working...this could take a few seconds...").queue();

        //Each Message has a list of MessageReactions, and each MessageReaction has a list of users
        for (int i = 0; i < validMovies.size(); i++) {
            List<MessageReaction> reactions = validMovies.get(i).getReactions();

            for (MessageReaction reaction : reactions) {

                //If it isn't a valid reaction, move to the next one
                if (!MyUtils.isValidReaction(reaction.getReactionEmote().getName())) {
                    continue;
                }
                //If we find the user listed in a reaction, we count that as a rating
                if (reaction.getUsers().stream().anyMatch(s -> s.equals(thisUser))) {
                    numOfUserRatings[i]++;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(thisUser.getName()).append(" 's Movie Completion: \n");

        //We've counted the number of ratings per movie, if it's 0 then they haven't
        // rated it, and if it's 2+ then they've rated more than once
        for (int i = 0; i < numOfUserRatings.length; i++) {
            if (numOfUserRatings[i] == 0) {
                stringBuilder.append("You have not rated ");
                stringBuilder.append(validMovies.get(i).getContentDisplay());
                stringBuilder.append("\n");
            } else if (numOfUserRatings[i] >= 2) {
                stringBuilder.append("You have rated ");
                stringBuilder.append(validMovies.get(i).getContentDisplay());
                stringBuilder.append(" more than once!\n");
            }
        }
        return stringBuilder.toString();
    }
}
