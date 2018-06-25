
/**
 * Enumeration of valid Commands in the Game.
 *
 */
public enum CommandWord
{
    GO("go"),

    QUIT("quit"),

    HELP("help"),

    UNKNOWN("?");

    String commandString;
    
    CommandWord(String commandString){
        this.commandString = commandString;
    }

    public String toString(){
        return commandString;
    }
}
