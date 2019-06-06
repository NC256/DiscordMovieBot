import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public final class MyUtils {

    //This class should not be instantiated
    private MyUtils() {
    }

    /**
     * @param channelName Name of the channel being searched for
     * @param thisGuild   Guild that the channel exists in
     * @return The TextChannel that matches the channelName being searched for, or null if not found
     */
    static TextChannel getTextChannelByName(String channelName, Guild thisGuild) {
        List<TextChannel> channelList = thisGuild.getTextChannelsByName(channelName, true);
        if (channelList.size() == 1) {
            return channelList.get(0);
        } else {
            return null;
        }
    }

    /**
     * @param channel TextChannel to search though
     * @return A List of messages that have one or more MessageReactions attached
     */
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

    /**
     * @param movieChannel Channel that the movies (Messages) are in
     * @return A List of all movies that have at least one valid rating and are not flagged as unwatched (red dot)
     */
    static List<Message> getValidMovies(TextChannel movieChannel) {
        List<Message> validMovies = getMessagesWithReactions(movieChannel);

        //-1 is any movie with a red dot (not counted), so it's removed here
        //-1 is also any movie without at least 1 valid rating
        validMovies.removeIf(m -> getAverageMovieRating(m) == -1);

        return validMovies;
    }

    /**
     * @param movieChannel Channel that the movies (Messages) are in
     * @return A List of all movies in the movieChannel that have a red dot reaction
     */
    static List<Message> getRedDotMovies(TextChannel movieChannel) {
        List<Message> redDots = getMessagesWithReactions(movieChannel);
        redDots.removeIf(m -> !isRedDotMovie(m));
        return redDots;
    }

    /**
     * @param movie Message to evaluate for a red dot
     * @return True if red dot is found, false otherwise
     */
    private static boolean isRedDotMovie(Message movie) {
        List<MessageReaction> reactions = movie.getReactions();
        for (MessageReaction reaction : reactions) {
            if (isRedDot(reaction.getReactionEmote().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param movie The movie (Message) to be scored
     * @return The average rating score of the movie
     */
    static double getAverageMovieRating(Message movie) {
        List<MessageReaction> reactions = movie.getReactions();
        double numOfReactions = 0;
        double totalValue = 0;

        for (MessageReaction mr : reactions) {
            String reactionName = mr.getReactionEmote().getName();

            if (isRedDot(reactionName)){
                return -1.0;
            }
            if (isValidReaction(reactionName)) {
                numOfReactions += mr.getCount();

                // totalValue += rating number * total number of ratings
                totalValue += (getValidReactionValue(reactionName) * mr.getCount());
            }
        }
        //If, after counting the reactions, we find no ratings, return -1.0
        if (numOfReactions == 0) {
            return -1.0;
        } else {
            return (totalValue / numOfReactions);
        }
    }

    /**
     * @param name Name of the MessageReaction
     * @return True if it's a valid reaction, false otherwise
     */
    static boolean isValidReaction(String name) {
        return (name.equals("1⃣") || name.equals("2⃣") ||
                name.equals("3⃣") || name.equals("4⃣") ||
                name.equals("5⃣") || name.equals("6⃣") ||
                name.equals("7⃣") || name.equals("8⃣") ||
                name.equals("9⃣") || name.equals("\uD83D\uDD1F") ||
                name.equals("\u23F8") || name.equals("\uD83D\uDD5B") ||
                name.equals("0⃣"));
    }

    /**
     * Figures out what rating a user has given a movie and returns the value of that rating
     *
     * @param movie Movie to be checked
     * @param user  User to look for
     * @return The rating that the given user put on the movie, -1 if no rating given by that user
     */
    static int getUserRatingFromMovie(Message movie, User user) {

        List<MessageReaction> reactions = movie.getReactions();

        for (MessageReaction mr : reactions) {
            String reactionName = mr.getReactionEmote().getName();

            //If it's valid (aka, an actual rating)
            if (isValidReaction(reactionName)) {

                //Search for the user
                if (mr.getUsers().stream().anyMatch(s -> s.equals(user))) {
                    return getValidReactionValue(reactionName);
                }
            }
        }
        return -1;
    }

    /**
     * @param name  name of User being searched for
     * @param guild guild the User is in
     * @return reference to the User being searched for, null if User not found
     */
    static User getUserByName(String name, Guild guild) {
        List<Member> returnedUsers = guild.getMembersByName(name, true);

        if (returnedUsers.size() == 1) {
            return returnedUsers.get(0).getUser();
        } else if (returnedUsers.size() > 1) {
            return null;
        }
        //If none are found, try again with nicknames
        else  {
            List<Member> returnedNicknameUsers = guild.getMembersByNickname(name, true);

            //More than one match is found (probably because the user didn't type the full name
            if (returnedNicknameUsers.size() == 1){
                return returnedNicknameUsers.get(0).getUser();
            } else {
                return null;
            }
        }
    }

    /**
     * @param name Name of the reaction to check
     * @return true if it's the name of the red dot emote, false otherwise
     */
    private static boolean isRedDot(String name) {
        return name.equals("\uD83D\uDD34");
    }

    /**
     * @param name name of the reaction to be checked
     * @return numerical value of the rating, -1 if not valid
     */
    static int getValidReactionValue(String name) {
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

    /**
     * @param args     String array to be partially or wholly recombined
     * @param startpos where in the array to start the new string
     * @return A single string of several array indexes added together
     */
    static String rebuildUsername(String[] args, int startpos) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = startpos; i < args.length; i++) {

            //Don't append a space if it's the last word
            if (i == args.length - 1) {
                stringBuilder.append(args[i]);
            } else {
                stringBuilder.append(args[i]).append(" ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * A method that processes user input for a command
     *
     * @param args  the string array of arguments passed to the command
     * @param user  the user that sent the command
     * @param guild the guild the user and command are in
     * @return Reference to the user being targeted by the command. Defaults to the user who sent the command if another not found
     */
    static User getUserByInput(String[] args, User user, Guild guild) {

        //If there are only two arguments, the second one is supposed to be the username
        if (args.length == 2) {
            return getUserByName(args[1], guild);
        }
        //More than that means we have to reconstruct the name
        else if (args.length > 2) {
            return getUserByName(rebuildUsername(args, 1), guild);
        }
        //No name provided, assume the user is the argument
        else {
            return user;
        }
    }
}