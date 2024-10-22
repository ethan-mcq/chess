package model;

import chess.ChessGame;

public record gameData(int gameID, String gameName, ChessGame game, String whiteUsername, String blackUsername) {

}