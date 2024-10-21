package model;

import chess.ChessGame; //I think? So we can get all the board and pieces

public record game (int gameID, String gameName, ChessGame game, String whiteUser, String blackUser) {

}