import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class MovieRandom implements Command {
    //!randomMovie #channelName

    MessageReceivedEvent message;
    String[] args;

    MovieRandom(MessageReceivedEvent message, String[] args) {
        this.message = message;
        this.args = args;
    }


    public String execute() {

        Guild thisGuild = message.getGuild();

        //Declare variables
        StringBuilder returnString = new StringBuilder();
        String channelName;


        //Make sure user gave us a channel argument
        channelName = args[0];


        //Go get the channel they specified
        TextChannel movieChannel = MyUtils.getTextChannelByName(channelName, thisGuild);
        if (movieChannel == null) {
            return "Cannot find that channel";
        }

        //Get a list of all red dot movies
        List<Message> movies = MyUtils.getRedDotMoviesFromTextChannel(movieChannel);

        if (movies.size() == 0) {
            return "You've watched all the movies!";
        }

        //Initiate a Random object
        Random myRand = new Random();

        //I'm garbage for stacking all these calls but basically:
        //Get a random movie from the List and get it's name and return that
        returnString.append(movies.get(myRand.nextInt(movies.size())).getContentDisplay());

        return returnString.toString();
    }

}
