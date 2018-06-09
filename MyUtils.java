import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class MyUtils {

   //Gets a list of TextChannels and the name of a desired TextChannel and searches
   // through them by name and returns the TextChannel with the matching name
   //Returns null if it can't find the channel in the provided list
   static TextChannel getTextChannelByName(String channelName, List<TextChannel>
           channelList) {

      for (int i = 0; i < channelList.size(); i++) {
         if (channelList.get(i).getName().equals(channelName)) {
            return channelList.get(i);
         }
      }
      return null;
   }
}
