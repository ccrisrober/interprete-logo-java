package Dependencies.PR3;

import Dependencies.PR1.Symbols.VT;
import java.util.Objects;

public class Token extends VT {

    //Atributos.
    protected String lexema;
    protected Object contenido;

    //Constructores.
    public Token(String l, Object c) {
        this.lexema = l;
        this.contenido = c;
    }

    //Getter & Setter.
    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public Object getContenido() {
        return contenido;
    }

    public void setContenido(Object contenido) {
        this.contenido = contenido;
    }

    //Métodos.
    @Override
    public String toString() {
        return "<" + this.getLexema() + ", " + this.getContenido().toString() + ">";
    }

    public boolean lexemasIguales(String lexema) {
        return this.lexema.compareTo(lexema) == 0;
    }

    public boolean contenidosIguales(Object contenido) {
        if (contenido instanceof String) {
            return ((String) this.contenido).compareToIgnoreCase((String) contenido) == 0;
        }
        return this.contenido.equals(contenido);
    }

    @Override
    public boolean equals(Object o) {
        Token t = null;
        try {
            t = (Token) o;
        }
        catch (ClassCastException e) {
            System.err.println("Error de casteo.");
        }
        return (this.lexema.compareTo(t.lexema) == 0)
                && (this.contenido.equals(t.contenido));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.lexema);
        hash = 79 * hash + Objects.hashCode(this.contenido);
        return hash;
    }
}
