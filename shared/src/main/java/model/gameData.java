package model;

import chess.ChessGame;

public record gameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {

}