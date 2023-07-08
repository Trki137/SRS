package hr.fer.zemris.srs.usermgmt.console;

import hr.fer.zemris.srs.usermgmt.commands.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleImpl{

    private final Map<String, Command> commandMap;

    private final static String TERMINATE = "exit";

    public final static Path dbPath = Path.of("../database.txt");

    public ConsoleImpl(){
        this.commandMap = new HashMap<>();

        this.commandMap.put("add", new AddCommand());
        this.commandMap.put("forcepass", new ForcePasswordChangeCommand());
        this.commandMap.put("passwd", new ChangePasswordCommand());
        this.commandMap.put("del",new DeleteCommand());
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        ConsoleImpl console = new ConsoleImpl();

        if(!Files.exists(dbPath))
            Files.createFile(dbPath);

        while(true){
            System.out.print("> ");
            String newLine = scanner.nextLine().trim();

            if(newLine.isBlank()){
                System.out.println("Input can't be blank");
                continue;
            }
            if(newLine.equals(TERMINATE)) break;
            String[] commandLine = newLine.split(" ");

            if(commandLine.length != 2){
                System.out.println("Invalid command format. Expected command name and username");
                continue;
            }

            String command = commandLine[0];
            String username = commandLine[1];



            if(!console.commandMap.containsKey(command)) {
                System.out.println("Invalid command "+ command);
                continue;
            }

            try {
                console.commandMap.get(command).execute(username);
            }catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
                System.out.println("Couldn't execute command "+ command);
            }
        }
    }
}
