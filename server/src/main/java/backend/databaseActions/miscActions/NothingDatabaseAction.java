package backend.databaseActions.miscActions;

import backend.databaseActions.DatabaseAction;

import java.util.Random;

public class NothingDatabaseAction implements DatabaseAction {
    // it does nothing
    private static String [] funkyMessages = {"Error: Database has gone rogue and dropped off the face of the digital universe. Proceed with extreme caution or risk being sucked into the void.",

            "Warning: Database meltdown imminent. Prepare for a cataclysmic data purge that will leave you trembling in the darkest corners of cyberspace.",

            "Critical Error: Database implosion detected. Brace yourself for the apocalyptic unraveling of your digital existence. Abandon all hope, ye who enter here.",

            "Danger! Database self-destruct sequence initiated. Evacuate immediately or face the wrath of the binary gods as they wipe your data from existence.",

            "Fatal Error: Database meltdown in progress. Your precious information is evaporating faster than a fleeting nightmare. Pray for a miracle, but don't hold your breath.",

            "System Alert: Database corruption detected. Your worst nightmare has become a reality as your data crumbles into a digital wasteland. Time to call in the data exorcist.",

            "Error 404: Database has vanished into the abyss. Your data has been devoured by the void, leaving only echoes of lost information to haunt your sleepless nights."};

    public static String getRandomMessage() {
        Random random = new Random();
        int randIndex = random.nextInt(funkyMessages.length);
        return funkyMessages[randIndex];
    }

    @Override
    public Object actionPerform()  {
        return null;
    }
}
