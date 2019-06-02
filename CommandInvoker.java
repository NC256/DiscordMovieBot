import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;


//Invoker class
public class CommandInvoker {


    TextChannel channelReceived;
    Message sentMessage;
    String messageString;

    Map<String, Command> commandMap = new HashMap<>();

    CommandInvoker(MessageReceivedEvent commandEvent) {

        channelReceived = commandEvent.getTextChannel();
        sentMessage = commandEvent.getMessage();
        messageString = sentMessage.getContentDisplay();

        //Gets the end index of the first word/command out of the message
        int endOfCommandWord = messageString.indexOf(" ");

        //If no arguments exist after the command
        if (endOfCommandWord == -1) {
            channelReceived.sendMessage("No arguments found").queue();
            return;
        }

        String commandWord = messageString.substring(0, endOfCommandWord);

        //Get all the arguments after the command word (+1 eats the empty space)
        String[] args = messageString.substring(commandWord.length() + 1).split(" ");

        commandMap.put("!d", new DiceRoller(commandEvent, args));
        commandMap.put("!rankings", new MovieRankings(commandEvent, args));
        commandMap.put("!myRankings", new MoviePersonalRankings(commandEvent, args));
        commandMap.put("!movieCompletion", new MovieCompletion(commandEvent, args));
        commandMap.put("!movieDetails", new MovieDetails(commandEvent, args));
        commandMap.put("!randomMovie", new MovieRandom(commandEvent, args));
        commandMap.put("!secretSanta", new SecretSanta(commandEvent, args));


        Command currentCommand = commandMap.get(commandWord);
        if (currentCommand == null) {
            channelReceived.sendMessage("Command not found.").queue();
            return;
        } else {
            channelReceived.sendMessage(currentCommand.execute()).queue();
        }
    }
}
