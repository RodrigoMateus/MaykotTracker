package com.maykot.radiolibrary;

public enum Command {
    RESET("reset");

    String command;

    Command(String command) { this.command = command; }

    public String getCommand() { return command; }
}
