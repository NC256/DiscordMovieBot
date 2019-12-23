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

        String[] names = args;

        ArrayList<String> shuffledNames = new ArrayList<>(Arrays.asList(names));
        ArrayList<String> originalNames = new ArrayList<>(Arrays.asList(names));


        boolean duplicates = true;
        int escape = 0;

        //While they aren't shuffled correctly, keep shuffling and checking
        while (duplicates) {

            duplicates = false;

            Collections.shuffle(shuffledNames);

            for (int i = 0; i < names.length; i++) {
                if (shuffledNames.get(i).equals(originalNames.get(i))) {
                    duplicates = true;
                    break;
                }
            }

            escape++;
            if (escape == 1000) {
                return "Error: Try again";
            }
        }

        // OriginalNames[1] *gets* ShuffledNames[1] as the person they are gifting to
        //Private message the names to everyone
        for (int i = 0; i < names.length; i++) {
            final int ii = i;
            User user = MyUtils.getUserByName(originalNames.get(i), message.getGuild());
            if (user == null) {
                return "Can't find any user by name: " + originalNames.get(i);
            }
            try {
                user.openPrivateChannel().queue((channel) -> channel.sendMessage(shuffledNames.get(ii)).queue());
            } catch (NullPointerException e) {
                e.printStackTrace();
                return "Error: Could not private message: " + originalNames.get(i) + "!";
            }

        }
        return "Check your messages!";
    }
}
