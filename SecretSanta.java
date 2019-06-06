import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Very quick and dirty class to do secret santa given a list of usernames.
 */
public class SecretSanta implements Command {
    //!secretSanta [comma separated name list]

    MessageReceivedEvent message;
    String[] args;

    SecretSanta(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    @Override
    public String execute() {

        String[] names = args[0].split(",");

        ArrayList<String> shuffledNames = new ArrayList<>(Arrays.asList(names));

        boolean duplicates = true;
        int escape = 0;

        //While they aren't shuffled correctly, keep shuffling and checking
        while (duplicates) {

            duplicates = false;

            Collections.shuffle(shuffledNames);
            for (int i = 0; i < names.length; i++) {
                if (shuffledNames.get(i).equals(names[i])) {
                    duplicates = true;
                }
            }

            escape++;
            if (escape == 100) {
                return "Error: Try again";
            }
        }

        //Private message the names to everyone
        for (int i = 0; i < names.length; i++) {
            final int ii = i;
            User user = MyUtils.getUserByName(names[i], message.getGuild());
            try {
                user.openPrivateChannel().queue((channel) -> {
                    channel.sendMessage(shuffledNames.get(ii)).queue();
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
                return "Error: Could not private message everyone!";
            }

        }
        return "Check your messages!";
    }
}
