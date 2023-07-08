package hr.fer.zemris.srs.commands;

import java.nio.file.Path;

public interface Command {
    void run(String commandLine, Path path);
}
