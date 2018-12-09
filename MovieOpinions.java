import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieOpinions {


   static String opinions(String messageDisplay, List<TextChannel> channelList) {

       //StringBuilder for crafting the return message
       //String[] input for splitting each word in the command
      StringBuilder movieOpinions = new StringBuilder();
      String[] input = messageDisplay.split(" ");
      String channelName;
      String movieName = null;
      Message mainMovie = null;

       //If incorrectly entered, return error, otherwise mark down the channel name
      if (input.length > 1) {
         channelName = input[1];
      } else {
          return "No channel name provided or you need a space before the channel name";
      }

       //Movie names can have spaces in them, so the name needs to be reassembled
      if (input.length > 2) {
         for (int i = 2; i < input.length; i++) {
            if (i == 2) {
               movieName = input[2];
            } else {
               movieName += " " + input[i];
            }
         }
      }

      if(movieName == null){
          return "No movie name provided";
      }

       //Getting reference to the channel with all the movies in it
      TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, channelList);
      if (movieChannel == null) {
         return "Cannot find that channel";
      }

       //Get all movies from that channel
      List<Message> movies = MyUtils.getValidMoviesFromTextChannel(movieChannel);

       //Look through the list for our movie
      for (int i = 0; i < movies.size(); i++) {
         if (movies.get(i).getContentDisplay().toLowerCase().startsWith(movieName.toLowerCase())) {
            mainMovie = movies.get(i);
         }
      }
      if (mainMovie == null) {
         return "Could not find that movie.";
      }


       //Append movie name to top of return message
      movieOpinions.append(mainMovie.getContentDisplay());
      movieOpinions.append("'s ratings:");
      movieOpinions.append("\n");

       //Get all reactions from the movie we've found
       List<MessageReaction> oldReactions = mainMovie.getReactions();
       ArrayList<MessageReaction> reactions = new ArrayList<>();


       //Copy all reactions into an ArrayList, due to mainMovie.getReactions() returning
       // an immutable list
       for (int i = 0; i < oldReactions.size(); i++) {
           reactions.add(oldReactions.get(i));
       }

       //Remove all invalid MessageReactions
       reactions.removeIf(i -> !MyUtils.isValidReaction(i.getReactionEmote().getName()));

       //While my list of reactions isn't empty
       while (!reactions.isEmpty()) {

           int highest = -1;
           int highestIndex = -1;

           //This loop gets the highest value reaction in the list
           for (int i = 0; i < reactions.size(); i++) {
               int currentReactionValue = MyUtils.getValidReactionValue(reactions.get(i).getReactionEmote().getName());
               //if it's higher than the highest so far
               if (currentReactionValue > highest) {
                   highest = currentReactionValue;
                   highestIndex = i;
               }
         }

           //Name (emoji as string)
           String highestReactionName = reactions.get(highestIndex).getReactionEmote().getName();

         //Use this garbage to get the users for the current reaction
           Object[] objectArray = reactions.get(highestIndex).getUsers().stream().toArray();

           //Only need usernames, so turning that into an array so we can alphabetize them
           String[] userNameArray = new String[objectArray.length];


           //Moving them all over, requires casting back to a User object first
           for (int i = 0; i < objectArray.length; i++) {
               User temp = (User) objectArray[i];
               userNameArray[i] = temp.getName();
           }

           //Alphabetize names
           Arrays.sort(userNameArray);

           //Append the highest rated emoji
           movieOpinions.append(highestReactionName);
           movieOpinions.append(":");
           movieOpinions.append("\n");

           //For the list of users on this reaction, append them
           for (int k = 0; k < objectArray.length; k++) {
               movieOpinions.append("-       ");
               movieOpinions.append(userNameArray[k]);
               movieOpinions.append("\n");
           }
           //Remove processed emoji from list of reactions
           reactions.remove(highestIndex);
      }
      return movieOpinions.toString();
   }
}
