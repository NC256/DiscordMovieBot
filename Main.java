import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {

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

        //Does not reply to bot messages
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

        //Gets the user that sent the message
        User thisUser = event.getAuthor();

        //If you've sent a help message I'm gonna print this string
        if (messageDisplay.equals("!help")) {

            //You may notice that every single .sendMessage() ends with a .queue();
            // This is because Discord has some measures to avoid spam, and JDA handles
            // this automatically if you tack on .queue() at the end of every .sendMessage();
            thisChannel.sendMessage("Try the following commands:\n\n" + "`!d num` " + "returns a random number between 0 and num\n"

                    + "`!rankings channelName` " + "returns a ranking of all the movies in `channelName`\n"

                    + "`!myRankings channelName optionalUsername` " + "Delivers your personal rankings or the rankings of `optionalUsername`\n"

                    + "`!checkCompletion channelName optionalUsername` " + "Returns a list of movies that you haven't rated or have accidentally rated twice. \n"

                    + "`!opinions channelName movieName`" + " Returns the ratings on the given movie\n"

                    + "`!randomMovie channelName`" + " Gives you a random red dot movie from `channelName`\n"

                    + "`!secretSanta [comma separated names (no spaces)]` " + "Secret Santa Command :santa:").queue();
        }

        //!d #num
        //Returns a random number between 0 and #num
        if (messageDisplay.startsWith("!d")) {
            thisChannel.sendMessage(DiceRoller.roll(messageDisplay)).queue();
        }

        //!rankings #channelName
        //Returns a ranking of all the movies in #channelName
        if (messageDisplay.startsWith("!" + "rankings")) {
            thisChannel.sendMessage(MovieRankings.rankings(messageDisplay, channelList)).queue();
        }

        //!myRankings #channelName #optionalUsername
        //Returns personal rankings or the rankings of #optionalUsername
        if (messageDisplay.startsWith("!myRankings")) {
            thisChannel.sendMessage(MoviePersonalRankings.check(messageDisplay, channelList, thisUser, thisGuild)).queue();
        }

        //!checkCompletion #channelName #optionalUsername
        //Returns a list of movies you haven't rated or have accidentally rated twice
        if (messageDisplay.startsWith("!checkCompletion")) {
            thisChannel.sendMessage(MovieCompletion.completion(messageDisplay, channelList, thisUser, thisGuild)).queue();
        }

        //!opinions #channelName #movieName
        //Returns a person's ratings of the given movie
        if (messageDisplay.startsWith("!opinions")) {
            thisChannel.sendMessage(MovieOpinions.opinions(messageDisplay, channelList)).queue();
        }

        //!randomMovie #channelName
        //Returns a random red dot movie from #channelName
        if (messageDisplay.startsWith("!randomMovie")) {
            thisChannel.sendMessage(MovieRandom.getMovie(messageDisplay, channelList)).queue();
        }

        //!secretSanta [comma separated name list]
        if (messageDisplay.startsWith("!secretSanta")) {
            SecretSanta.sendSecrets(messageDisplay, thisGuild);
            thisChannel.sendMessage("The secrets have been delivered! :christmas_tree:").queue();
        }
    }
}
