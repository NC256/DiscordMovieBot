import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {

        JDABuilder builder = new JDABuilder(AccountType.BOT);

        //The TokenGiver class does not exist on my Github because it contains a single
        // method that returns the token of my bot. If you had this token you could
        // pretend to be my bot and do stuff with it. You'll need your own token for
        // running your own bot
        String token = TokenGiver.returnToken();
        builder.setToken(token);

        //This adds an eventListener so that whenever a message comes in this class will
        // be sent the event
        builder.addEventListener(new Main());

        //This logs the bot in
        builder.buildAsync();
    }

    //Overriding a method in the ListenerAdapter class so I can receive new messages
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println(event.getAuthor() + event.getMessage().getContentDisplay());

        if (!event.getMessage().getContentDisplay().startsWith("!")) {
            return;
        }

        //Does not reply to messages sent by bots (in order to avoid any potential infinite loops)
        if (event.getAuthor().isBot()) {
            return;
        }

        //If the message I just received is a TextChannel object, then check if I can talk in
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

        //If you've sent a help message I'm gonna print this string
        if (messageDisplay.equals("!help")) {

            //You may notice that every single .sendMessage() ends with a .queue();
            // This is because Discord has some measures to avoid spam, and JDA handles
            // this automatically if you tack on .queue() at the end of every .sendMessage();
            thisChannel.sendMessage("Try the following commands:\n\n"
                    + "`!d num` " + "returns a random number between 0 and num\n"

                    + "`!rankings channelName` " + "returns a ranking of all the movies in `channelName`\n"

                    + "`!myRankings channelName optionalUsername` " + "Delivers your personal rankings or the rankings of `optionalUsername`\n"

                    + "`!movieCompletion channelName optionalUsername` " + "Returns a list of movies that you haven't rated or have accidentally rated twice. \n"

                    + "`!movieDetails channelName movieName`" + " Returns the ratings on the given movie\n"

                    + "`!randomMovie channelName`" + " Gives you a random red dot movie from `channelName`\n"

                    + "`!secretSanta [comma separated names (no spaces)]` " + "Secret Santa Command :santa:").queue();

            return;
        }

        //Fire the command handler
        new CommandInvoker(event);
    }
}
