package sample.chess.pieces;

import static sample.chess.util.Movement.jump;

import sample.chess.Game;
import sample.chess.Move;
import javafx.scene.paint.Color;

public class Knight extends Piece {

    //CONSTRUCTOR
    public Knight(Color color) {
        super(color);
    }

    //METHODS
    //knight can jump one row and two columns or the inverse.
    public boolean checkMove(Game g, Move m) {
        return super.checkMove(g, m) && jump.test(m);
    }

    @Override
    public Piece makeCopy() {
        return new Knight(getColor());
    }


}
