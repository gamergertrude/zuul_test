/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room airport, 
        street, entrance, corridor, kitchen, livingRoom, guestRoom, bathroom, stairs, 
        basement, 
        secondFloorStairs,secondFloorCorridor, secondFloorBathroom, childsRoom, masterBedroom, secondFloorLivingRoom, 
        attic;

        // create the rooms
        airport =new Room ("in the airport");
        
        street = new Room("on the street");
        entrance = new Room("in the house");
        corridor = new Room("in the corridor");
        kitchen = new Room("in the kitchen");
        livingRoom = new Room("in the living room");
        guestRoom = new Room("in the guest room");
        bathroom = new Room("in the bathroom");
        stairs = new Room("on the stairs");
        
        basement = new Room("in the basement");
        
        secondFloorStairs = new Room("on the 2nd floor's stairs");
        secondFloorCorridor = new Room("in the 2nd floor");
        secondFloorBathroom = new Room("in the 2nd floor bathroom");
        childsRoom = new Room("in the child's room");
        masterBedroom = new Room ("in the master bedroom");
        secondFloorLivingRoom = new Room ("in the living room");
        
        attic = new Room ("in the attic");
        
        // initialise room exits
        //first floor
        street.setExit("north", airport);
        street.setExit("south", entrance);
        
        entrance.setExit("north", street);
        entrance.setExit("south", corridor);
        
        corridor.setExit("northeast", entrance);
        corridor.setExit("northwest", bathroom);
        corridor.setExit("east", kitchen);
        corridor.setExit("southeast", livingRoom);
        corridor.setExit("southwest", guestRoom);
        corridor.setExit("west", stairs);
        
        kitchen.setExit("west", corridor);
        kitchen.setExit("southwest", livingRoom);
        
        livingRoom.setExit("north", corridor);
        livingRoom.setExit("northeast", kitchen);
        livingRoom.setExit("west", guestRoom);
        
        guestRoom.setExit("north", corridor);
        guestRoom.setExit("east", livingRoom);
        
        bathroom.setExit("south", corridor);
        
        stairs.setExit("up", secondFloorStairs);
        stairs.setExit("down", basement);
        
        //basement
        basement.setExit("up", stairs);
        
        //second floor
        secondFloorStairs.setExit("up", attic);
        secondFloorStairs.setExit("east", corridor);
        secondFloorStairs.setExit("down", stairs);
        
        secondFloorCorridor.setExit("northwest", secondFloorLivingRoom);
        secondFloorCorridor.setExit("east", masterBedroom);
        secondFloorCorridor.setExit("southeast", secondFloorBathroom);
        secondFloorCorridor.setExit("southwest", childsRoom);
        
        secondFloorLivingRoom.setExit("south", secondFloorCorridor);
        
        masterBedroom.setExit("west", secondFloorCorridor);
        
        bathroom.setExit("north", secondFloorCorridor);
        bathroom.setExit("west", childsRoom);
        
        childsRoom.setExit("north", secondFloorCorridor);
        childsRoom.setExit("east", secondFloorBathroom);
        
        //attic
        attic.setExit("down", secondFloorStairs);

        currentRoom = street;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
            if (!finished)
            {
                System.out.println(processCommand(command));
            }
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * This is a further method added by BK to
     * provide a clearer interface that can be tested:
     * Game processes a commandLine and returns output.
     * @param commandLine - the line entered as String
     * @return output of the command
     */
    public String processCommand(String commandLine){
        Command command = parser.getCommand(commandLine);     
        return processCommand(command);
    }
    
    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private String printHelp() 
    {
        return "You are lost. You are alone. You wander"
        +"\n"
        + "around at the university."
        +"\n"
        +"\n"
        +"Your command words are:"
        +"\n"
        +"   go quit help"
        +"\n";
    }

    /** 
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private String goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            return "Go where?";
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);
        String result = "";
        if (nextRoom == null) {
            result += "There is no door!";
        }
        else {
            currentRoom = nextRoom;
            result += "You are " + currentRoom.getDescription()+"\n";
            result += "Exits: ";
            if(currentRoom.getExit("north") != null) {
                result += "north ";
            }
            if(currentRoom.getExit("east") != null) {
                result += "east ";
            }
            if(currentRoom.getExit("south") != null) {
                result += "south ";
            }
            if(currentRoom.getExit("west") != null) {
                result += "west ";
            }         
        }
        return result + "\n";
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
