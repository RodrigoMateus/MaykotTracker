package com.maykot.maykottracker.radio;

public enum Command {
    RESET("reset");

    String command;

    Command(String command) { this.command = command; }

    public String getCommand() { return command; }
}
