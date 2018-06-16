import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class MovieRankings {

   static String rankings(String messageDisplay, List<TextChannel> channelList) {
      try {

         //This StringBuilder will hold the text that contains the list of movies
         // and rankings. You can do this with a String, but String concatenation is
         // expensive and StringBuilders are designed for letting you add strings
         // together and are much faster
         StringBuilder movieRankings = new StringBuilder();

         //Open code block
         movieRankings.append("```");

         //This gets the name of the channel that you type onto the end of the command
         String[] input = messageDisplay.split(" ");
         if(input.length==1){
            return "Could not find that channel.";
         }

         //Calls MyUtils class to search for the movie in the list of channels provided
         TextChannel movieChannel = MyUtils.getTextChannelByName(input[1],
                 channelList);

         //If that method was unable to find the channel then it returns null, and we
         // check that here
         if (movieChannel == null) {
            return "Could not find that channel.";
         }

         List<Message> validMovies = MyUtils.getValidMoviesFromTextChannel(movieChannel);

         //While my array of Movie objects isn't empty
         while (!validMovies.isEmpty()) {
            double highest = -1;
            int highestIndex = -1;

            //Loops over movies ArrayList until it finds the movie with the highest
            // average rating, which it then keeps track of that movie's score and
            // it's location in the ArrayList
            for (int i = 0; i < validMovies.size(); i++) {
               double currentAverage = MyUtils.getAverageMovieRating(validMovies.get(i));

               //If the movie we're looking at has a higher rating then we track that one
               if (currentAverage > highest) {
                  highest = currentAverage;
                  highestIndex = i;
               }
            }
            //We get the name and rating and format it to look nice
            String movieName = validMovies.get(highestIndex).getContentDisplay();
            double movieRating = MyUtils.getAverageMovieRating
                    (validMovies.get(highestIndex));

            movieRankings.append(String.format("%6.3f: %-50s\n", movieRating,
                    movieName));
            //After we've done that, we remove this Movie from the ArrayList so we
            // can loop over it again looking for the next highest ranked movie
            validMovies.remove(highestIndex);
         }

         //If our StringBuilder is empty after all the prior code has run, then we haven't
         // found a single valid movie
         if (movieRankings.toString().isEmpty()) {
            return "No movies found.";
         }

         //Close code block
         movieRankings.append("```");

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
