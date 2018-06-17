
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MoviePersonalRankings {

   //Currently garbage
   //Takes 5-10 seconds to get un-sorted results back for a set of like ~23 messages
   //I need a better way to iterating over the data, but I don't know what that way is

   static String check(String messageDisplay, List<TextChannel> channelList, User
           thisUser, Guild thisGuild){
      StringBuilder movieStatus = new StringBuilder();
      movieStatus.append("```");
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
      int currentReactionValue;
      String currentReactionName;

      ArrayList<Integer> ratings = new ArrayList<>();
      ArrayList<String> names = new ArrayList<>();

      //For each movie
      for(int i = 0; i < movies.size(); i++){
         int userRating = MyUtils.getUserRatingFromMovie(movies.get(i), thisUser);
         if(userRating == -1){
            //The user did not rate the current movie
         }
         else{
            ratings.add(userRating);
            names.add(movies.get(i).getContentDisplay());
         }
      }

      //Sorting output
      while(!ratings.isEmpty()){
         int highest = -1;
         int highestIndex = -1;

         for(int i = 0; i < ratings.size(); i++){
            if(ratings.get(i) > highest){
               highest = ratings.get(i);
               highestIndex = i;
            }
         }
         movieStatus.append(String.format("%2d: %-50s\n", ratings.get(highestIndex),
                 names.get(highestIndex)));
         names.remove(highestIndex);
         ratings.remove(highestIndex);
      }
      movieStatus.append("```");
      return movieStatus.toString();
   }
}

