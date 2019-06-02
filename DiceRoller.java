import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;

public class DiceRoller implements Command {

    MessageReceivedEvent message;
    String[] args;

    DiceRoller(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }

    @Override
    public String execute() {
        try {
            int maxNum = Integer.valueOf(args[0]);
            if (maxNum <= 0) {
                return "Whoops, you didn't enter a positive number!";
            }
            Random myRand = new Random();
            int returnValue = myRand.nextInt(maxNum) + 1;

            if (returnValue == maxNum) {
                return "Critical Success: " + maxNum + "!";
            } else if (returnValue == 1) {
                return "Critical fail: 1!";
            } else {
                return String.valueOf(returnValue);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "Please enter an integer between 0 and 2,147,483,648!";
        } catch (NullPointerException e1) {
            e1.printStackTrace();
            return "Please enter a number after the command!";
        }
    }
}
