import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class MovieCompletion {

   //This class checks for the following:
   //Rating twice
   //Not rating
   //

   static String completion(String messageDisplay, List<TextChannel> channelList, User
           thisUser, Guild thisGuild) {

      StringBuilder movieStatus = new StringBuilder();
      String[] input = messageDisplay.split(" ");
      String username = null;
      String channelName;
      if (input.length > 1) {
         channelName = input[1];
      } else {
         return "No channel name provided or you need a space " +
                 "before the channel name";
      }
      if(input.length > 2){
         for(int i = 2; i < input.length; i++){
            if(i == 2){
               username = input[2];
            }
            else if (i > 2){
               username += " " + input[i];
            }
         }
      }

      TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, channelList);
      if (movieChannel == null) {
         return "Cannot find that channel";
      }
      List<Message> movies = MyUtils.getValidMoviesFromTextChannel(movieChannel);
      int[] numOfRatings = new int[movies.size()];
      if (username == null) {
         movieStatus.append(thisUser.getName());
         movieStatus.append("'s Movie Completion:\n");
      }
      else {
         thisUser = MyUtils.getUserByName(username, thisGuild);
         if (thisUser == null) {
            return "Person not found or multiple users with that name.";
         } else {
            movieStatus.append(thisUser.getName());
            movieStatus.append("'s Movie Completion:\n");
         }
      }
      //For each movie
      int currentReactionValue;
      String currentReactionName;
      long startLoop = System.currentTimeMillis();
      for (int i = 0; i < movies.size(); i++) {

         //Get a list of all reactions
         List<MessageReaction> reactions = movies.get(i).getReactions();

         //For each reaction
         middleLoop:
         for (int k = 0; k < reactions.size(); k++) {

            //Get its name
            currentReactionName = reactions.get(k).getReactionEmote().getName();

            //If it's a valid reaction, get its value
            if (MyUtils.isValidReaction(currentReactionName)) {
               currentReactionValue = MyUtils.getValidReactionValue
                       (currentReactionName);
            }

            //If it isn't, move to the next reaction
            else {
               continue;
            }

            //Use this garbage to get the users for the current reaction
            Object[] userArray = reactions.get(k).getUsers().stream().toArray();

            //For the list of users on this reaction
            for (int j = 0; j < userArray.length; j++) {
               if (userArray[j].equals(thisUser)) {
                  numOfRatings[i]++;
               }
            }
         }
      }

      //We've counted the number of ratings per movie, if it's 0 then they haven't
      // rated it, and if it's 2+ then they've rated more than once
      for (int i = 0; i < numOfRatings.length; i++) {
         if (numOfRatings[i] == 0) {
            movieStatus.append("You have not rated ");
            movieStatus.append(movies.get(i).getContentDisplay());
            movieStatus.append("\n");
         }
         else if (numOfRatings[i] == 1) {
            //Do nothing
         }
         else if (numOfRatings[i] >= 2) {
            movieStatus.append("You have rated ");
            movieStatus.append(movies.get(i).getContentDisplay());
            movieStatus.append(" more than once!\n");
         }
      }
      return movieStatus.toString();
   }

}
