import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.List;

public class RankMovies {

   static String rankings(String messageDisplay, List<TextChannel> channelList) {
      try {

         //This StringBuilder will hold the text that contains the list of movies
         // and rankings. You can do this with a String, but String concatenation is
         // expensive and StringBuilders are designed for letting you add strings
         // together and are much faster
         StringBuilder movieRankings = new StringBuilder();

         //Arraylist is like a fancy array, where you don't need to declare a size
         // and can keep adding more boxes onto the end at any time (and remove
         // boxes from anywhere in the array at any time)
         ArrayList<Movie> movies = new ArrayList<>();

         //This gets the name of the channel that you type onto the end of the command
         String channelNameArg = messageDisplay.substring(14);

         //Calls MyUtils class to search for the movie in the list of channels provided
         TextChannel movieChannel = MyUtils.getTextChannelByName(channelNameArg,
                 channelList);

         //If that method was unable to find the channel then it returns null, and we
         // check that here
         if (movieChannel == null) {
            return "Could not find that channel.";
         }

         //Gets a list of all messages sent in current channel, from the bottom up
         for (Message message : movieChannel.getIterableHistory()) {

            //Makes a list of reactions from the current Message
            //Discord has two types of reactions, Emojis and Emotes. Emotes are
            // those custom reactions you see on servers, sometimes they are animated.
            // Emojis are the Unicode standard characters
            List<MessageReaction> reactions = message.getReactions();

            //If message has no reactions, continue to next message
            if (reactions.isEmpty()) {
               continue;
            }

            //Initialize new Movie object and place it in the movies ArrayList!
            //Turns out if you just slap the new keyword in there you don't need to
            // give each Movie object a name
            movies.add(new Movie(message));
         }

         //While my array of Movie objects isn't empty
         while (!movies.isEmpty()) {
            double highest = -1;
            int highestIndex = -1;

            //Loops over movies ArrayList until it finds the movie with the highest
            // average rating, which it then keeps track of that movie's score and
            // it's location in the ArrayList
            for (int i = 0; i < movies.size(); i++) {
               double currentAverage = movies.get(i).getAverageRating();

               //CurrentAverage returns -1.0 when the movie has a red dot or has no
               // valid ratings, in which case we throw it out
               if (currentAverage == -1.0) {
                  movies.remove(i);
                  break;
               }

               //If the movie we're looking at has a higher rating then we pay attention
               // to that one
               if (currentAverage > highest) {
                  highest = currentAverage;
                  highestIndex = i;
               }
            }
            //We then append the name of the movie and it's score to our
            // StringBuilder we declared at the top of this section
            //First we check that we have a valid movie though
            if (highestIndex != -1) {
               movieRankings.append(movies.get(highestIndex).getName());
               movieRankings.append(": ");
               movieRankings.append(movies.get(highestIndex).getAverageRating());
               movieRankings.append("\n");
               //After we've done that, we remove this Movie from the ArrayList so we
               // can loop over it again looking for the next highest ranked movie
               movies.remove(highestIndex);
            }
         }

         //If our StringBuilder is empty after all the prior code has run, then we haven't
         // found a single valid movie
         if (movieRankings.toString().isEmpty()) {
            return "No movies found.";
         }
         //Finally we use the .toString() method of our StringBuilder to return the
         // results
         return movieRankings.toString();
      }

      //This usually happens when the user doesn't provide a channel name, in which
      // case our subString method call at the top that looks for the channel name
      // tries to look outside of the string we received, and throws this exception
      catch (IndexOutOfBoundsException e1) {
         return "No channel name provided or you need a space " +
                 "before the channel name";
      }
   }
}
