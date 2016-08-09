package Interface;

/**
 * Write a description of class Lienzo here.
 *
 * @author Francisco Dominguez
 * @date 14/03/2013
 * @version 20130314
 */
import Analyzer.Syntax.Interpreter;
import Dependencies.PR3.Token;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

public class Canvas extends JPanel {

    /**
     * Constructor for objects of class Lienzo
     */
    List<Token> instrucciones;

    public Canvas() {
        super();
        setPreferredSize(new Dimension(320, 240));
        instrucciones = new ArrayList<Token>();
    }

    public Canvas(List<Token> instr) {
        super();
        setPreferredSize(new Dimension(320, 240));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.black);
        Turtle t = new Turtle(g);
        if (instrucciones.size() > 0) {
            Interpreter interprete = new Interpreter(t);
            try {
                interprete.interpretar(instrucciones);
                Window.ponerAnalizarAceptado();
            }
            catch (RuntimeException ei) {
                Window.ponerAnalizarFallido(ei.getLocalizedMessage());
                //Si encuentra fallo, borra todo lo ejecutado
                this.redraw(new LinkedList<Token>());
                this.removeAll();
            }
        }
    }

    public void redraw(List<Token> listaTokens) {
        instrucciones = listaTokens;
        this.repaint();
    }
}
