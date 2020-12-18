package com.webcheckers.model;

import java.util.Random;

public class Game {

    public static int NON_CAPTURE_MOVES_TO_TIE_GAME = 70;
    private Player player1;
    private Player player2;

    private String player1name;
    private String player2name;

    private PieceColor player1color;
    private PieceColor player2color;

    private int playerIdCurrentlyPlaying;
    private int winnerPlayerId;
    private int noCaptureMovesCounter;
    //determines if a block is the reason for winning
    private boolean blockageFlag;
    //determines if a resign is the reason for winning
    private boolean resignFlag;
    //used with /backupMove
    private boolean isPlayerSkipping;

    private Message message;

    private Board board;
    private Board pendingBoard;

    private GameState state;

    private boolean isMoveCapturingPiece;

    /**
     * Constructor
     */
    public Game(Player currentPlayer, Player opponentPlayer)
    {
        //setting players
        this.player1 = currentPlayer;
        this.player2 = opponentPlayer;
        this.player1name = currentPlayer.getName();
        this.player2name = opponentPlayer.getName();
        //creating board
        this.board = new Board();
        this.pendingBoard = new Board();
        this.state = GameState.IDLE;
      //setting defaults
        this.winnerPlayerId = 0;//0 means no winner yet
        this.noCaptureMovesCounter=0;
        this.isMoveCapturingPiece = false;
        this.blockageFlag = false;
        this.resignFlag = false;
        this.isPlayerSkipping = false;
    }

    /**
     * Getters/Setters
     */

    public Player getPlayer(int playerId) {
        if(playerId == 1){ return player1; } else{ return player2; }
    }

    public String getPlayerName(int playerId) {
        if(playerId == 1){ return player1name; } else{ return player2name; }
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
        if(playerId == playerIdCurrentlyPlaying){return true; } else {return false;}
    }

    public Board getBoard() { return this.board; }
    public void setBoard(Board board) { this.board = board; }


    public Board getPendingBoard() { return this.pendingBoard; }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getOpponentId(int playerId){ if(playerId==1){return 2;}else{return 1;}}
    public int getWinnerPlayerId(){return this.winnerPlayerId;}
    public void setWinnerPlayerId(int playerId){this.winnerPlayerId=playerId;}
    public boolean isWinner(int playerId){return playerId==winnerPlayerId;}
    public void trackMove(Move move){

    }
    public void incrementNoCaptureMovesCounter(){this.noCaptureMovesCounter++;}
    public void resetNoCaptureMovesCounter(){this.noCaptureMovesCounter=0;}
    public int getNoCaptureMovesCounter(){return this.noCaptureMovesCounter;}
    public void setBlockageFlag(boolean param){this.blockageFlag = param;}
    public boolean getBlockageFlag(){return this.blockageFlag;}
    public void setResignFlag(boolean param){this.resignFlag = param;}
    public boolean getResignFlag(){return this.resignFlag;}
    public void setIsPlayerSkippingFlag(boolean param){this.isPlayerSkipping = param;}
    public boolean getIsPlayerSkippingFlag(){return this.isPlayerSkipping;}

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


    public void resetPendingBoard()
    {
        this.pendingBoard = this.board.clone();
    }

    public void commitPendingBoard()
    {
        this.board = this.pendingBoard.clone();
    }

    public void switchTurn() {
        this.playerIdCurrentlyPlaying = (this.playerIdCurrentlyPlaying % 2) + 1;
    }

}
