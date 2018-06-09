public class UnicodeGarbage {

   public static boolean isRating(String name) {

      //This method checks if the name of the reaction is one of the valid rating emojis
      if (name.equals("1⃣") || name.equals("2⃣") || name.equals("3⃣") || name.equals
              ("4⃣") || name.equals("5⃣") || name.equals("6⃣") || name.equals("7⃣") ||
              name.equals("8⃣") || name.equals("9⃣") || name.equals("\uD83D\uDD1F") ||
              name.equals("\u23F8") || name.equals("\uD83D\uDD5B") || name.equals("0⃣")) {
         return true;
      } else {
         return false;
      }
   }

   //Red dot movies are ones that we haven't watched yet, and should not be given a rating
   public static boolean isRedCircle(String name) {

      if (name.equals("\uD83D\uDD34")) {
         return true;
      } else {
         return false;
      }
   }

   //This method returns an integer value for each of the valid emojis
   public static int getValue(String name) {
      switch (name) {
         case "1⃣":
            return 1;
         case "2⃣":
            return 2;
         case "3⃣":
            return 3;
         case "4⃣":
            return 4;
         case "5⃣":
            return 5;
         case "6⃣":
            return 6;
         case "7⃣":
            return 7;
         case "8⃣":
            return 8;
         case "9⃣":
            return 9;
         case "\uD83D\uDD1F":
            return 10;
         case "\u23F8":
            return 11;
         case "\uD83D\uDD5B":
            return 12;
         case "0⃣":
            return 0;
         default:
            return -1;
      }
   }
}
