package com.biobattle;

import android.util.Log;

public class TowerScript {
    private boolean isRunning;

    public TowerScript() {
        isRunning = true; // Initialize the script as running
    }

    public void stop() {
        // Logic to stop or halt the tower script's execution
        isRunning = false;
        Log.d("Deleted", "Deleted");
        // Additional logic to stop any ongoing processes or threads
    }

    public boolean isRunning() {
        return isRunning;
    }
}