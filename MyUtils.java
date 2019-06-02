import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class MyUtils {

    //Returns the textchannel that is being looked for
    public static TextChannel getTextChannelByName(String channelName, Guild thisGuild) {

        List<TextChannel> channelList = thisGuild.getTextChannelsByName(channelName, true);
        if (channelList.size() == 1) {
            return channelList.get(0);
        } else {
            return null;
        }
    }

    //Gets all messages from channel that have at least one reaction
    //Is pretty fast
    private static List<Message> getMessagesWithReactions(TextChannel channel) {
        ArrayList<Message> reacted = new ArrayList<>();
        for (Message message : channel.getIterableHistory()) {
            List<MessageReaction> reactions = message.getReactions();
            if (reactions.isEmpty()) {
                continue;
            }
            reacted.add(message);
        }
        return reacted;
    }

    //Returns all valid movies
    public static List<Message> getValidMoviesFromTextChannel(TextChannel movieChannel) {

        List<Message> validMovies = getMessagesWithReactions(movieChannel);

        //-1 is any movie with a red dot (not counted), so it's removed here
        //-1 is also any movie without at least 1 valid rating
        validMovies.removeIf(m -> getAverageMovieRating(m) == -1);

        return validMovies;
    }

    //Returns a list of movies that have red dot reactions
    public static List<Message> getRedDotMoviesFromTextChannel(TextChannel movieChannel) {
        List<Message> redDots = getMessagesWithReactions(movieChannel);

        //Removes movie if it isn't a red dot movie
        redDots.removeIf(m -> !isRedDotMovie(m));

        return redDots;
    }

    //Returns true if the given Message movie has a red dot
    public static boolean isRedDotMovie(Message movie) {

        //List of reactions
        List<MessageReaction> reactions = movie.getReactions();

        //For each reaction
        for (MessageReaction reaction : reactions) {

            //If we find a red dot, return true
            if (isRedCircle(reaction.getReactionEmote().getName())) {
                return true;
            }
        }
        return false;
    }

    //Returns the average score of the movie passed
    public static double getAverageMovieRating(Message movie) {
        //Make list of reactions
        List<MessageReaction> reactions = movie.getReactions();
        double numOfReactions = 0;
        double totalValue = 0;

        //For each reaction
        for (int i = 0; i < reactions.size(); i++) {
            MessageReaction reaction = reactions.get(i);
            String reactionName = reaction.getReactionEmote().getName();

            //If we find a red circle, return -1.0
            if (isRedCircle(reactionName)) {
                return -1.0;
            }

            //If the reaction is valid, add the number of times it's submitted to the
            // total and add it's value to the ongoing total
            if (isValidReaction(reactionName)) {
                numOfReactions += reaction.getCount();

                // totalValue += value of reaction * total number of reactions
                totalValue += (getValidReactionValue(reactionName) * reaction.getCount());
            }
        }
        //If, after counting the reactions, we find no ratings, return -1.0
        if (numOfReactions == 0) {
            return -1.0;
        } else {
            return (totalValue / numOfReactions);
        }
    }

    //Checks if reaction is valid movie rating
    public static boolean isValidReaction(String name) {

        //This method checks if the name of the reaction is one of the valid rating emojis
        if (name.equals("1⃣") || name.equals("2⃣") || name.equals("3⃣") || name.equals("4⃣") || name.equals("5⃣") || name.equals("6⃣") || name.equals("7⃣") || name.equals("8⃣") || name.equals("9⃣") || name.equals("\uD83D\uDD1F") || name.equals("\u23F8") || name.equals("\uD83D\uDD5B") || name.equals("0⃣")) {
            return true;
        } else {
            return false;
        }
    }

    //Gets the reaction value for a given user and given movie
    public static int getUserRatingFromMovie(Message movie, User user) {

        //This method is rather slow despite some attempts at optimization

        //Get a list of all reactions(emotes) on the given message
        List<MessageReaction> reactions = movie.getReactions();

        //For each reaction on the current message
        for (MessageReaction mr : reactions) {

            //get name
            String name = mr.getReactionEmote().getName();

            //If it's valid (aka, an actual rating)
            if (MyUtils.isValidReaction(name)) {

                //If any user in the stream of users who responded with that reaction matches the user we're looking for, then bingo
                if (mr.getUsers().stream().anyMatch(s -> s.equals(user))) {
                    return MyUtils.getValidReactionValue(name);
                }
            }
        }
        return -1;
    }

    //Returns a user from a Guild based on name
    public static User getUserByName(String name, Guild guild) {

        //Gets users by original names
        List<Member> returnedUsers = guild.getMembersByName(name, true);
        if (returnedUsers.size() > 1) {
            return null;
        }
        //If none are found, try again with nicknames
        else if (returnedUsers.size() == 0) {
            List<Member> returnedNicknameUsers = guild.getMembersByNickname(name, true);

            //More than one match is found (probably because the user didn't type the full name
            if (returnedNicknameUsers.size() > 1) {
                return null;
            } else if (returnedNicknameUsers.size() == 0) {
                return null;
            } else {
                return returnedNicknameUsers.get(0).getUser();
            }
        } else {
            return returnedUsers.get(0).getUser();
        }
    }

    //Red dot movies are ones that we haven't watched yet, and should not be given a rating
    //This maybe shouldn't exist?
    public static boolean isRedCircle(String name) {

        return name.equals("\uD83D\uDD34");
    }

    //This method returns an integer value for each of the valid reactions
    public static int getValidReactionValue(String name) {
        switch (name) {
            case "1⃣":
                return 1;
            case "2⃣":
                return 2;
            case "3⃣":
                return 3;
            case "4⃣":
                return 4;
            case "5⃣":
                return 5;
            case "6⃣":
                return 6;
            case "7⃣":
                return 7;
            case "8⃣":
                return 8;
            case "9⃣":
                return 9;
            case "\uD83D\uDD1F":
                return 10;
            case "\u23F8":
                return 11;
            case "\uD83D\uDD5B":
                return 12;
            case "0⃣":
                return 0;
            default:
                return -1;
        }
    }

    public static String rebuildUsername(String[] args, int startpos) {

        StringBuilder builder = new StringBuilder();

        for (int i = startpos; i < args.length; i++) {
            if (i == args.length - 1) {
                builder.append(args[i]);
            } else {
                builder.append(args[i]);
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}
