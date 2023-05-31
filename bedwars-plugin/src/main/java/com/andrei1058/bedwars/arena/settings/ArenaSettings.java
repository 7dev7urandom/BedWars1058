package com.andrei1058.bedwars.arena.settings;

public class ArenaSettings {
    private boolean playersNicked = false;

    public ArenaSettings() {}
    public boolean isPlayersNicked() {
        return playersNicked;
    }

    public void setPlayersNicked(boolean playersNicked) {
        this.playersNicked = playersNicked;
    }
    public void togglePlayersNicked() {
        this.playersNicked = !playersNicked;
    }
}
