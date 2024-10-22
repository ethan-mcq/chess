package model;

import chess.ChessGame;

public record gameData(int gameID, String gameName, ChessGame game, String whiteUser, String blackUser) {

}