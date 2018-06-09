import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;

import java.util.List;

public class Movie {

   private Message movieMessage;

   public Movie(Message message) {
      this.movieMessage = message;
   }

   double getAverageRating() {

      //Make list of reactions
      List<MessageReaction> reactions = movieMessage.getReactions();
      double numOfReactions = 0;
      double totalValue = 0;

      //For each reaction
      for (int i = 0; i < reactions.size(); i++) {
         MessageReaction reaction = reactions.get(i);

         //If we find a red circle, return -1.0
         if (UnicodeGarbage.isRedCircle(reaction.getReactionEmote().getName())) {
            return -1.0;
         }

         //If the reaction is valid, add the number of times it's submitted to the
         // total and add it's value to the ongoing total
         if (UnicodeGarbage.isRating(reaction.getReactionEmote().getName())) {
            numOfReactions += reaction.getCount();
            totalValue += (UnicodeGarbage.getValue(reaction.getReactionEmote().getName()
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

   String getName() {
      return movieMessage.getContentDisplay();
   }


}
