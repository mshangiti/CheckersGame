package com.webcheckers.model;

import java.util.Random;

public class Game {

    public static  String PLAYER_ID_ATT = "playerId";

    private Player player1;
    private Player player2;

    private String player1name;
    private String player2name;

    private PieceColor player1color;
    private PieceColor player2color;


    private int playerIdCurrentlyPlaying;

    //TODO: Message object

    private Board board;

    /**
     * Constructor
     */
    public Game(Player currentPlayer, Player opponentPlayer)
    {
        this.player1 = currentPlayer;
        this.player2 = opponentPlayer;

        this.player1name = currentPlayer.getName();
        this.player2name = opponentPlayer.getName();

        board = new Board();
    }

    /**
     * Getters/Setters
     */

    public Player getPlayer(int playerId) {
        if(playerId==1){ return player1; } else{ return player2; }
    }

    public String getPlayerName(int playerId) {
        if(playerId==1){ return player1name; } else{ return player2name; }
    }

    public boolean isPlayerInGame(String name){
        if(player1name.equalsIgnoreCase(name) || player2name.equalsIgnoreCase(name)){
            return true;
        }
        return false;
    }

    public PieceColor getPlayerColor(int playerId) {
        if(playerId==1){ return player1color; } else{ return player2color; }
    }

    public boolean isMyTurn(int playerId) {
        if(playerId==playerIdCurrentlyPlaying){return true;}else{return false;}
    }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    /**
     * Methods
     */

    public void InitializeGame() {
        int color = new Random().nextInt(PieceColor.values().length);
        this.player1color = PieceColor.values()[color];
        this.player2color = PieceColor.values()[(color + 1) % 2 ];

        if (player1color == PieceColor.WHITE)
        {
            playerIdCurrentlyPlaying = 1;
        }
        else
        {
            playerIdCurrentlyPlaying = 2;
        }
    }

}
