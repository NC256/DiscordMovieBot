import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

class CommandInvoker {

    /**
     * This class processes and executes a command that this bot has received.
     *
     * @param commandEvent The message event that was received
     */
    public static void processCommand(MessageReceivedEvent commandEvent) {

        TextChannel channelReceived = commandEvent.getTextChannel();
        String messageString = commandEvent.getMessage().getContentDisplay();

        int endOfCommandWord = messageString.indexOf(" ");

        //Every command has at least one argument, so this can be done
        if (endOfCommandWord == -1) {
            channelReceived.sendMessage("No arguments found").queue();
            return;
        }

        //The actual command word after the exclamation mark
        String commandWord = messageString.substring(0, endOfCommandWord);

        //Get all the arguments after the command word (+1 consumes the empty space after the command word)
        String[] args = messageString.substring(commandWord.length() + 1).split(" ");

        //Mapping command names to command objects
        Map<String, Command> commandMap = new HashMap<>();
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
        } else {
            channelReceived.sendMessage(currentCommand.execute()).queue();
        }
    }
}
