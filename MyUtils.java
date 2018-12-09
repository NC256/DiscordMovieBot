import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;


import java.util.ArrayList;
import java.util.List;

public class MyUtils {

   //Gets a list of TextChannels and the name of a desired TextChannel and searches
   // through them by name and returns the TextChannel with the matching name
   //Returns null if it can't find the channel in the provided list
   public static TextChannel getTextChannelByName(String channelName, List<TextChannel>
           channelList) {

      for (int i = 0; i < channelList.size(); i++) {
         if (channelList.get(i).getName().equals(channelName)) {
            return channelList.get(i);
         }
      }
      return null;
   }

   //This can probably be done better, but having the structure in place is the most
   // important thing for right now
   public static List<Message> getValidMoviesFromTextChannel(TextChannel movieChannel){
      ArrayList<Message> movieCandidates = new ArrayList<>();
      for (Message message : movieChannel.getIterableHistory()){
         List<MessageReaction> reactions = message.getReactions();
         if (reactions.isEmpty()) {
            continue;
         }
         movieCandidates.add(message);
      }

      //This nifty little line is called a Predicate, added in Java 8
      //It will look at my entire movieCandidates ArrayList and remove anything that
      // returns a -1 for it's average rating. This avoids loops and removing elements
      // and literally does all of it in one line
      movieCandidates.removeIf(m -> getAverageMovieRating(m) == -1);

      return movieCandidates;
   }

   //Returns a list of movies that have red dot reactions
   public static List<Message> getRedDotMoviesFromTextChannel(TextChannel movieChannel){
      ArrayList<Message> movieCandidates = new ArrayList<>();
      for (Message message : movieChannel.getIterableHistory()){
         List<MessageReaction> reactions = message.getReactions();
         if (reactions.isEmpty()) {
            continue;
         }
         movieCandidates.add(message);
      }
      //Removes movie if it isn't a red dot movie
      movieCandidates.removeIf(m -> !isRedDotMovie(m));

      return movieCandidates;
   }

   //Returns true if the given Message movie has a red dot
   public static boolean isRedDotMovie (Message movie){

      //List of reactions
      List<MessageReaction> reactions = movie.getReactions();

      //For each reaction
      for (int i = 0; i < reactions.size(); i++){
         MessageReaction reaction = reactions.get(i);

         //If we find a red dot, return true
         if (isRedCircle(reaction.getReactionEmote().getName())){
            return true;
         }
         else{
            continue;
         }
      }

      return false;
   }

   //Returns the average score of the movie passed
   public static double getAverageMovieRating (Message movie){
      //Make list of reactions
      List<MessageReaction> reactions = movie.getReactions();
      double numOfReactions = 0;
      double totalValue = 0;

      //For each reaction
      for (int i = 0; i < reactions.size(); i++) {
         MessageReaction reaction = reactions.get(i);

         //If we find a red circle, return -1.0
         if (isRedCircle(reaction.getReactionEmote().getName())) {
            return -1.0;
         }

         //If the reaction is valid, add the number of times it's submitted to the
         // total and add it's value to the ongoing total
         if (isValidReaction(reaction.getReactionEmote().getName())) {
            numOfReactions += reaction.getCount();
            totalValue += (getValidReactionValue(reaction.getReactionEmote().getName()
            ) * reaction.getCount());
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
      if (name.equals("1⃣") || name.equals("2⃣") || name.equals("3⃣") || name.equals
              ("4⃣") || name.equals("5⃣") || name.equals("6⃣") || name.equals("7⃣") ||
              name.equals("8⃣") || name.equals("9⃣") || name.equals("\uD83D\uDD1F") ||
              name.equals("\u23F8") || name.equals("\uD83D\uDD5B") || name.equals("0⃣")) {
         return true;
      } else {
         return false;
      }
   }

   //Gets the reaction value for a given user and given movie
   public static int getUserRatingFromMovie(Message movie, User user) {

      List<MessageReaction> reactions = movie.getReactions();
      for (MessageReaction mr: reactions) {
         String currentReactionName = mr.getReactionEmote().getName();
         int currentReactionValue = -1;

         //If not a valid reaction, skip to the next one
         if (MyUtils.isValidReaction(currentReactionName)) {
            currentReactionValue = MyUtils.getValidReactionValue(currentReactionName);
         } else {
            continue;
         }
         Object[] userArray = mr.getUsers().stream().toArray();

         for (int k = 0; k < userArray.length; k++) {
            if (userArray[k].equals(user)) {
               return currentReactionValue;
            }
         }
      }
      return -1;
   }

   //Returns a user from a Guild based on name
   public static User getUserByName(String name, Guild guild){
      List<Member> returnedUsers = guild.getMembersByName(name, true);
      if (returnedUsers.size() > 1){
         return null;
      }
      else if (returnedUsers.size() == 0){
         List<Member> returnedNicknameUsers = guild.getMembersByNickname(name, true);
         if(returnedNicknameUsers.size() > 1){
            return null;
         }
         else if (returnedNicknameUsers.size() == 0){
            return null;
         }
         else{
            return returnedNicknameUsers.get(0).getUser();
         }
      }
      else{
         return returnedUsers.get(0).getUser();
      }
   }

   //Red dot movies are ones that we haven't watched yet, and should not be given a rating
   //This maybe shouldn't exist?
   public static boolean isRedCircle(String name) {

      if (name.equals("\uD83D\uDD34")) {
         return true;
      } else {
         return false;
      }
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
}
