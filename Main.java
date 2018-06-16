import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Main extends ListenerAdapter {

   public static void main(String[] args) throws LoginException {

      //Creates new JDABuilder, which is used for the back-end heavy lifting of letting
      // Discord know your bot exists, logging in, etc
      JDABuilder builder = new JDABuilder(AccountType.BOT);

      //The TokenGiver class does not exist on my Github because it contains a single
      // method that returns the token of my bot. If you had this token you could
      // pretend to be my bot and do stuff with it. You'll need your own token for
      // making your own bot
      String token = TokenGiver.returnToken();
      builder.setToken(token);

      //This adds an eventListener so that whenever a message comes in this class will
      // be sent the relevant information.
      builder.addEventListener(new Main());

      //This logs the bot in
      builder.buildAsync();
   }

   //Overriding a method in the ListenerAdapter class so I can receive new messages
   @Override
   public void onMessageReceived(MessageReceivedEvent event) {
      System.out.println(event.getAuthor() + event.getMessage().getContentDisplay());

      //If a bot sent a message, I'm not going to reply (because infinite loops are scary)
      if (event.getAuthor().isBot()) {
         return;
      }

      //If the message I just received is a TextChannel, then check if I can talk in
      // the TextChannel, and if I can't, do nothing.
      //I check what channel type I'm in first because if I call .getTextChannel() on a
      // message that wasn't sent in a TextChannel, then there's nothing to get and we
      // get an error instead
      if (event.getChannelType().toString().equals("TEXT")) {
         if (!event.getTextChannel().canTalk()) {
            return;
         }
      }

      //This puts the message I received into a string so I can refer to it later
      String messageDisplay = event.getMessage().getContentDisplay();

      //This gets the current MessageChannel so I can refer to it later
      //It's important to remember that a MessageChannel and a TextChannel are two
      //separate things. MessageChannel is the generic "Channel you can send messages to"
      //There are several types of MessageChannel's, including TextChannel's,
      // PrivateChannels, and Groups
      MessageChannel thisChannel = event.getChannel();

      //In discord-ese, a Guild is the same thing as a Server
      Guild thisGuild = event.getGuild();

      //Gets list of all TextChannels in guild (server) and puts them in a List
      List<TextChannel> channelList = thisGuild.getTextChannels();

      User thisUser = event.getAuthor();

      //If you've sent a help message I'm gonna print this string
      if (messageDisplay.equals("!NThelp")) {

         //You may notice that every single .sendMessage() ends with a .queue();
         // This is because Discord has some measures to avoid spam, and JDA handles
         // this automatically if you tack on .queue() at the end of every .sendMessage();
         thisChannel.sendMessage("I am currently under construction!\nTry the " +
                 ("following commands:\n\n`!NTd num` returns a random number between 0 " +
                         "and num\n`!NTrankings channelName` returns a ranking of " +
                         "all the movies in `channelName`\n" + "`!NTmyRankings " +
                         "channelName optionalUsername` Delivers your personal " +
                         "rankings or the rankings of `optionalUsername`" +
                         " ")).queue();
      }

      //If the message we received starts with this text, do this stuff
      //To avoid this Main being 5000 lines long with all the code for every command,
      // most of them will be placed in other Classes

      //!NTrankings #channelName
      if (messageDisplay.startsWith("!NTrankings")) {

         //Sends the list of movies and received text to the server
         thisChannel.sendMessage(RankMovies.rankings(messageDisplay, channelList)).queue();
      }

      //!NTmyRankings #channelName #OptionalUsername
      if (messageDisplay.startsWith("!NTmyRankings")){
         thisChannel.sendMessage(CheckMovies.check(messageDisplay, channelList,
                 thisUser, thisGuild))
                 .queue();
      }

      //Sends a random number to the server
      if (messageDisplay.startsWith("!NTd")) {
         thisChannel.sendMessage(DiceRoller.roll(messageDisplay)).queue();
      }
   }
}
