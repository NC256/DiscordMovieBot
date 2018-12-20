import java.util.Random;

public class DiceRoller {

    public static String roll(String messageDisplay) {
        try {
            int maxNum = Integer.valueOf(messageDisplay.substring(3));
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

        } catch (NumberFormatException e1) {
            return "Please enter an integer between 0 and 2,147," + "483,648!";
        }
    }

}
