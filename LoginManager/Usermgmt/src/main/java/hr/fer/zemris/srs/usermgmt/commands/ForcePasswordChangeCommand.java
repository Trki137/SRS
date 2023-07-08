package hr.fer.zemris.srs.usermgmt.commands;

import hr.fer.zemris.srs.usermgmt.console.ConsoleImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class ForcePasswordChangeCommand implements Command {
    @Override
    public void execute(String username) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        try (BufferedReader br = Files.newBufferedReader(ConsoleImpl.dbPath)) {
            String line = br.readLine();

            while (!Objects.isNull(line)) {
                String[] data = line.split(",");

                if (!data[0].equals(username)) {
                    sb.append(line).append("\n");
                    line = br.readLine();
                    continue;
                }
                String newLine = data[0] + "," +
                        data[1] + "," +
                        data[2] + "," +
                        "1";

                sb.append(newLine).append("\n");
                found = true;

                line = br.readLine();

            }

            Files.writeString(ConsoleImpl.dbPath, sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Couldn't open file");
            return;
        }

        if(found) System.out.println("User will be requested to change password on next login.");
        else System.out.println("User doesn't exist");
    }
}
