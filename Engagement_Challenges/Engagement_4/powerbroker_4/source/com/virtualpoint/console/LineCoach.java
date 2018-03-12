package com.virtualpoint.console;

import java.io.PrintStream;

/**
 * Handles lines that don't seem to be commands
 */
public interface LineCoach {

    void handleLine(String line, PrintStream out);

}
