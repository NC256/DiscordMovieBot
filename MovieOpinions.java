import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;
import java.util.List;

public class MovieOpinions {


   static String opinions(String messageDisplay, List<TextChannel> channelList) {

      StringBuilder movieOpinions = new StringBuilder();
      String[] input = messageDisplay.split(" ");
      String channelName;
      String movieName = null;
      Message mainMovie = null;
      if (input.length > 1) {
         channelName = input[1];
      } else {
         return "No channel name provided or you need a space " +
                 "before the channel name";
      }

      if (input.length > 2) {
         for (int i = 2; i < input.length; i++) {
            if (i == 2) {
               movieName = input[2];
            } else if (i > 2) {
               movieName += " " + input[i];
            }
         }
      }
      if(movieName == null){
         return "Could not find that movie";
      }

      TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, channelList);
      if (movieChannel == null) {
         return "Cannot find that channel";
      }
      List<Message> movies = MyUtils.getValidMoviesFromTextChannel(movieChannel);

      for (int i = 0; i < movies.size(); i++) {
         if (movies.get(i).getContentDisplay().toLowerCase().startsWith(movieName.toLowerCase())) {
            mainMovie = movies.get(i);
         }
      }
      if (mainMovie == null) {
         return "Could not find that movie.";
      }
      movieOpinions.append(mainMovie.getContentDisplay());
      movieOpinions.append("'s ratings:");
      movieOpinions.append("\n");

      List<MessageReaction> reactions = mainMovie.getReactions();

      for (int i = 0; i < reactions.size(); i++) {
         String currentReactionName = reactions.get(i).getReactionEmote().getName();

         //If it's a valid reaction, get its value
         if (MyUtils.isValidReaction(currentReactionName)) {
            int currentReactionValue = MyUtils.getValidReactionValue
                    (currentReactionName);
         }

         //If it isn't, move to the next reaction
         else {
            continue;
         }

         //Use this garbage to get the users for the current reaction
         Object[] objectArray = reactions.get(i).getUsers().stream().toArray();

         //Convert that array back to an array of User objects
         User[] userArray = Arrays.copyOf(objectArray, objectArray.length, User[].class);

         //For the list of users on this reaction
         for (int k = 0; k < userArray.length; k++) {
            movieOpinions.append(userArray[k].getName());
            movieOpinions.append(" rated it a ");
            movieOpinions.append(currentReactionName);
            movieOpinions.append("\n");
         }
      }

      return movieOpinions.toString();
   }
}
