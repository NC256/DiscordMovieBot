import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.Random;

public class MovieRandom {

   static String getMovie (String messageDisplay, List<TextChannel> channelList ) {

      //Declar variables
      String[] input = messageDisplay.split(" ");
      StringBuilder movieRandom = new StringBuilder();
      String channelName;


      //Make sure user gave us a channel argument
      if (input.length > 1) {
         channelName = input[1];
      } else {
         return "No channel name provided or you need a space " +
                 "before the channel name";
      }

      //Go get the channel they specified
      TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, channelList);
      if (movieChannel == null) {
         return "Cannot find that channel";
      }

      //Get a list of all red dot movies
      List<Message> movies = MyUtils.getRedDotMoviesFromTextChannel(movieChannel);

      //Initiate a Random object
      Random myRand = new Random();

      //I'm garbage for stacking all these calls but basically:
      //Get a random movie from the List and get it's name and return that
      movieRandom.append(movies.get(myRand.nextInt(movies.size())).getContentDisplay());

      return movieRandom.toString();
   }

}
