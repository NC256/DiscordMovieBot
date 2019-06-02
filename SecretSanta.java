import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;

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

        Guild thisGuild = message.getGuild();

        //Names into array
        String[] names = args[0].split(",");

        ArrayList<String> randomMatches = new ArrayList<String>();

        //Add names into arraylist
        for (int i = 0; i < names.length; i++) {
            randomMatches.add(names[i]);
        }

        boolean noDuplicates = true;
        int escape = 0;

        //While they aren't shuffled correctly, keep shuffling and checking
        while (noDuplicates) {

            noDuplicates = false;

            Collections.shuffle(randomMatches);
            for (int i = 0; i < names.length; i++) {
                if (randomMatches.get(i).equals(names[i])) {
                    noDuplicates = true;
                }
            }
            escape++;
            if (escape == 500) {
                System.out.println("Broken");
            }
        }

        //Private message the names to everyone
        for (int i = 0; i < names.length; i++) {
            final int ii = i;
            User user = MyUtils.getUserByName(names[i], thisGuild);
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(randomMatches.get(ii)).queue();
            });
        }
        return "Check your messages!";
    }
}
