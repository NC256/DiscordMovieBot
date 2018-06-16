
import net.dv8tion.jda.core.entities.*;

import java.util.List;


public class MoviePersonalRankings {

   //Currently garbage
   //Takes 5-10 seconds to get un-sorted results back for a set of like ~23 messages
   //I need a better way to iterating over the data, but I don't know what that way is

   static String check(String messageDisplay, List<TextChannel> channelList, User
           thisUser, Guild thisGuild){
      StringBuilder movieStatus = new StringBuilder();
      String[] input = messageDisplay.split(" ");
      String username = null;
      String channelName;
      if(input.length > 1){
         channelName = input[1];
      }
      else{
         return "No channel name provided or you need a space " +
                 "before the channel name";
      }

      //Because we split on spaces and usernames can contain spaces, we must treat
      // everything past the username input as one single username and reconstruct it
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
      if(movieChannel == null){
         return "Cannot find that channel";
      }
      List<Message> movies = MyUtils.getValidMoviesFromTextChannel(movieChannel);
      if(username == null) {
         movieStatus.append(thisUser.getName());
         movieStatus.append("'s Movie Ratings:\n");
      }
      else{
         thisUser = MyUtils.getUserByName(username, thisGuild);
         if(thisUser == null){
            return "Person not found or multiple users with that name.";
         }
         else{
            movieStatus.append(thisUser.getName());
            movieStatus.append("'s Movie Ratings:\n");
         }
      }
      //For each movie
      int currentReactionValue;
      String currentReactionName;
      long startLoop = System.currentTimeMillis();
      for(int i = 0; i < movies.size(); i++){

         //Get a list of all reactions
         List<MessageReaction> reactions = movies.get(i).getReactions();

         //For each reaction
         middleLoop:
         for(int k = 0; k < reactions.size(); k++){

            //Get its name
            currentReactionName = reactions.get(k).getReactionEmote().getName();

            //If it's a valid reaction, get its value
            if(MyUtils.isValidReaction(currentReactionName)) {
               currentReactionValue = MyUtils.getValidReactionValue
                       (currentReactionName);
            }

            //If it isn't, move to the next reaction
            else{
               continue;
            }

            //Use this garbage to get the users for the current reaction
            Object[] userArray = reactions.get(k).getUsers().stream().toArray();

            //For the list of users on this reaction
            for(int j = 0; j < userArray.length; j++){
               if(userArray[j].equals(thisUser)){
                  movieStatus.append(movies.get(i).getContentDisplay());
                  movieStatus.append(" ");
                  movieStatus.append(currentReactionValue);
                  movieStatus.append("\n");

                  //This assumes the user has rated each movie only once and saves time
                  break middleLoop;
               }
            }
         }
      }
      long loopStopped = System.currentTimeMillis();
      movieStatus.append("That took " + (loopStopped - startLoop) + " milliseconds to " +
              "run.");
      return movieStatus.toString();
   }
}

