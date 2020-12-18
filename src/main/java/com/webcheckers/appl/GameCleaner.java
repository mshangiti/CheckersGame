package com.webcheckers.appl;

import java.util.Objects;
import com.webcheckers.model.Game;

public class GameCleaner implements Runnable {

    //
    // Attributes
    //
    private final GameCenter gameCenter;
    private final Game game;
    //
    // Constructor
    //

    GameCleaner (final GameCenter gameCenter, final Game game)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
        this.game = game;
    }

    public void run() {
        gameCenter.removeGame(this.game);
    }
}
