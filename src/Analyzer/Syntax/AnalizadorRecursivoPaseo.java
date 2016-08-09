package Analyzer.Syntax;

import Dependencies.PR1.Grammar;
import Dependencies.PR1.Symbols.VT;
import Dependencies.PR3.Token;
import Dependencies.PR5.SyntaxAnalyzer;
import Exceptions.Syntax.Extends.ColorException;
import Exceptions.Syntax.Extends.NewLineException;
import Exceptions.Syntax.Extends.OperatorException;
import Exceptions.Syntax.SyntaxException;
import java.io.File;
import java.util.Collection;
import java.util.List;

public class AnalizadorRecursivoPaseo extends SyntaxAnalyzer {

    protected Token tokenActual;
    protected List<Token> listaTokens;
    protected int posicion;
    protected int nLinea;
        
    public AnalizadorRecursivoPaseo(Grammar g) {
        super(g);
        posicion = 0;
        nLinea = 0;
    }

    @Override
    public boolean analizar(Collection<VT> listaTks) {
        this.listaTokens = (List)listaTks;
        nLinea = 0;
        posicion = 0;
        do {
            try {
                leerSiguienteToken();
                PASEO();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                return false;
            }
            if (!tokenActual.lexemasIguales("TK_FIN_SENT") && !tokenActual.lexemasIguales("TK_SALIDA")) {
                throw new NewLineException ("Encontrado '" + this.devolverErrorMatch(tokenActual.getLexema().substring(3)) + "', esperaba salto de línea.");
            }
            nLinea++;
        } while (!tokenActual.lexemasIguales("TK_SALIDA"));
        return true;
    }
   
    void PASEO() {
        if (esCasaGiroAvanzaPintaIDColorCondicionalSalto()) {
            PASO();
            PASEO();
        }
        //Consecuente = lambda
    }

    void PASO() {
        if (esCasa()) {
            Match(new VT("TK_CASA"));
            return;
        }
        if (esGiro()) {
            Match(new VT("TK_GIRO"));
            E();
            return;
        }
        if (esAvanza()) {
            Match(new VT("TK_AVANZA"));
            E();
            return;
        }
        if (esPinta()) {
            Match(new VT("TK_PINTA"));
            E();
            return;
        }
        if (esID()) {
            Match(new VT("TK_ID"));
            Match(new VT("TK_ASIGN"));
            E();
            return;
        }
        if (esColor()) {
            Match(new VT("TK_COLOR"));
            Match(new VT("TK_ASIGN"));
            COLOR();
            return;
        }
        if (esCondicional()) {
            Match(new VT("TK_SI"));
            CONDICIONAL();
            Match(new VT("TK_ENTONCES"));
            PASO();
            return;
        }
        if (esSalto()) {
            Match(new VT("TK_IR_A"));
            E();
            return;
        }
        throw new SyntaxException("Esperaba pinta, casa, avanza, giro, identificador, color, condicional (SI) o salto (ir_a).");
    }
    
    void CONDICIONAL () {
        
        NOT();
            
        if (esParentesisAbierto()) {
            COND_PAR();
        }
        else {
            COMP();
        }
    }
    
    void COND_PAR () {
        if (esParentesisAbierto()) {
            Match(new VT("TK_PAR_ABR"));
            CONDICIONAL();
            Match(new VT("TK_PAR_CER"));
            if (esAndOr()){
                AndOr();
            }
        }
    }
    
    void NOT () {
        if (esNot()) {
            Match (new VT("TK_NOT"));
            NOT();
        }
    }
    
    void AndOr() {
        if (esAnd()) {
            Match(new VT("TK_AND"));
            CONDICIONAL();
            return;
        }
        else if (esOr()) {
            Match(new VT("TK_OR"));
            CONDICIONAL();
            return;
        }
        throw new OperatorException("Esperaba AND o OR");
    } 
    
    void COMP () {
        E();
        if (esComparador()) {
            if (esMayor()) {
                Match(new VT("TK_MAYOR"));
            }
            else if (esMenor()) {
                Match(new VT("TK_MENOR"));
            }
            else if (esDistinto()) {
                Match(new VT("TK_DISTINTO"));
            }
            else if (esComparacion()) {
                Match(new VT("TK_IGUALDAD"));
            }
            else if (esMayorIgual()) {
                Match(new VT("TK_MAYOR_IGUAL"));
            }
            else if (esMenorIgual()) {
                Match(new VT("TK_MENOR_IGUAL"));
            }
            E();
            return;
        }
        throw new OperatorException("Esperaba comparador");
    }
    
    void COLOR () {
        if (esNegro()) {
            Match(new VT("TK_NEGRO"));
            return;
        }
        if (esVerde()) {
            Match(new VT("TK_VERDE"));
            return;
        }
        if (esNaranja()) {
            Match(new VT("TK_NARANJA"));
            return;
        }
        if (esRosa()) {
            Match(new VT("TK_ROSA"));
            return;
        }
        if (esRojo()) {
            Match(new VT("TK_ROJO"));
            return;
        }
        if (esBlanco()) {
            Match(new VT("TK_BLANCO"));
            return;
        }
        if (esAmarillo()) {
            Match(new VT("TK_AMARILLO"));
            return;
        }
        if (esMagenta()) {
            Match(new VT("TK_MAGENTA"));
            return;
        }
        throw new ColorException("Esperaba color");
    }
    
    void E() {
        T();
        if (esSumaResta()) {
            E_PRIMA();
        }
    }
    
    void E_PRIMA() {
        if (esSuma()) {
            Match(new VT("TK_MAS"));
            T();
            if (esSumaResta()) {
                E_PRIMA();
            }
            return;
        }
        else if (esResta()) {
            Match(new VT("TK_MENOS"));
            T();
            if (esSumaResta()) {
                E_PRIMA();
            }
            return;
        }
        throw new ArithmeticException ("Esperaba + o *");
    }
    
    void T () {
        F();
        if (esMultiplicacionDivision()) {
            T_PRIMA();
        }
    }
    
    void T_PRIMA() {
        if (esMultiplicacion()){
            Match(new VT("TK_PROD"));
            F();
            if (esMultiplicacionDivision()) {
                T_PRIMA();
            }
            return;
        }
        else if (esDivision()) {
            Match(new VT("TK_DIV"));
            F();
            if (esMultiplicacionDivision()) {
                T_PRIMA();
            }
            return;
        }
        throw new ArithmeticException ("Esperaba * o /");
    }
    
    void F () {
        if (esParentesisAbierto()) {
            Match(new VT("TK_PAR_ABR"));
            E();
            Match(new VT("TK_PAR_CER"));
            return;
        }
        else if (esID()) {
            Match(new VT("TK_ID"));
            return;
        }
        else if (esNumero()) {
            Match(new VT("TK_CTE_NUM"));
            return;
        }
        else if (esNumeroD()) {
            Match(new VT("TK_NOTCNTF"));
            return;
        }
        else if (esMasMenos()) {
            MAS_MENOS();
            F();
            return;
        }
        throw new SyntaxException("Línea " + nLinea + ": Esperaba paréntesis abierto, identificador o número.");
    }
    
    void MAS_MENOS () {
        if (esMas()) {
            Match(new VT("TK_MAS"));
            return;
        }
        else if (esMenos()) {
            Match(new VT("TK_MENOS"));
            return;
        }
        throw new ArithmeticException ("Espera + o -");
    }
    
    void Match(VT v) {
        if (tokenActual.lexemasIguales(v.getV())) {
            if (posicion != listaTokens.size() - 1) {
                leerSiguienteToken();
                return;
            }
            else {
                tokenActual = new Token("TK_SALIDA", "$#$");
                return;
            }
        }
        throw new SyntaxException ("Línea " + nLinea + ": Esperaba " + "'" + devolverErrorMatch(v.getV().substring(3)) + "'" + ", encontrado " + "'" 
                + (!tokenActual.contenidosIguales("TK_FIN_SENT")? tokenActual.getContenido() : File.separator + "r" + File.separator + "n") + "'.");
    }
    
    String devolverErrorMatch (String s) {
        if (s.compareToIgnoreCase("PAR_CER") == 0) {
            s = ")";
        }
        else if (s.compareToIgnoreCase("PAR_ABR") == 0) {
            s = "(";
        }
        else if (s.compareToIgnoreCase("MAYOR") == 0) {
            s = ">";
        }
        else if (s.compareToIgnoreCase("MENOR") == 0) {
            s = "<";
        }
        else if (s.compareToIgnoreCase("MAYOR_IGUAL") == 0) {
            s = ">=";
        }
        else if (s.compareToIgnoreCase("MENOR_IGUAL") == 0) {
            s = "<=";
        }
        else if (s.compareToIgnoreCase("DISTINTO") == 0) {
            s = "<>";
        }
        else if (s.compareToIgnoreCase("IGUALDAD") == 0) {
            s = "==";
        }
        else if (s.compareToIgnoreCase("MAS") == 0) {
            s = "+";
        }
        else if (s.compareToIgnoreCase("MENOS") == 0) {
            s = "-";
        }
        else if (s.compareToIgnoreCase("PROD") == 0) {
            s = "*";
        }
        else if (s.compareToIgnoreCase("DIV") == 0) {
            s = "/";
        }
        return s;
    }
    
    void leerSiguienteToken () {
        tokenActual = listaTokens.get(posicion);
        ++posicion;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Comparaciones">
    
        //<editor-fold defaultstate="collapsed" desc="COMPARADORS">

                boolean esComparador() {
                    return esMayor() || esMenor() || esDistinto() || esComparacion() || esMayorIgual() || esMenorIgual();
                }

                boolean esMenor() {
                    return tokenActual.lexemasIguales("TK_MENOR");
                }

                boolean esMayor() {
                    return tokenActual.lexemasIguales("TK_MAYOR");
                }

                boolean esComparacion() {
                    return tokenActual.lexemasIguales("TK_IGUALDAD");
                }

                boolean esDistinto() {
                    return tokenActual.lexemasIguales("TK_DISTINTO");
                }

                boolean esMayorIgual() {
                    return tokenActual.lexemasIguales("TK_MAYOR_IGUAL");
                }

                boolean esMenorIgual() {
                    return tokenActual.lexemasIguales("TK_MENOR_IGUAL");
                }

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Aritmética">

            boolean esMasMenos () {
                return esMas() || esMenos();
            }

            boolean esMas () {
                return tokenActual.lexemasIguales("TK_MAS");
            }

            boolean esMenos () {
                return tokenActual.lexemasIguales("TK_MENOS");
            }

            boolean esSumaResta () {
                return esMasMenos();
            }

            boolean esMultiplicacionDivision () {
                return esMultiplicacion() || esDivision();
            }

            boolean esSuma() {
                return esMas();
            }

            boolean esResta() {
                return esMenos();
            }

            boolean esMultiplicacion() {
                return tokenActual.lexemasIguales("TK_PROD");
            }

            boolean esDivision() {
                return tokenActual.lexemasIguales("TK_DIV");
            }

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Expresiones booleanas">

            boolean esAndOr() {
                return esAnd() || esOr ();
            }

            boolean esNot () {
                return tokenActual.lexemasIguales("TK_NOT");
            }

            boolean esAnd () {
                return tokenActual.lexemasIguales("TK_AND");
            }

            boolean esOr () {
                return tokenActual.lexemasIguales("TK_OR");
            }

        //</editor-fold>
    
        //<editor-fold defaultstate="collapsed" desc="PASO">

            boolean esCasaGiroAvanzaPintaIDColorCondicionalSalto() {
                return esCasa() || esGiro() || esAvanza() || esPinta() || esID() 
                        || esColor() || esCondicional() || esSalto();
            }
            
            boolean esEntonces() {
                return tokenActual.lexemasIguales("TK_ENTONCES");
            }  
    
            boolean esCasa() {
                return tokenActual.lexemasIguales("TK_CASA");
            }

            boolean esGiro() {
                return tokenActual.lexemasIguales("TK_GIRO");
            }

            boolean esAvanza() {
                return tokenActual.lexemasIguales("TK_AVANZA");
            }

            boolean esPinta() {
                return tokenActual.lexemasIguales("TK_PINTA");
            }

            boolean esID() {
                return tokenActual.lexemasIguales("TK_ID");
            }

            boolean esAsignacion() {
                return tokenActual.lexemasIguales("TK_ASIGN");
            }

            boolean esColor() {
                return tokenActual.lexemasIguales("TK_COLOR");
            }
            
            boolean esCondicional() {
                return tokenActual.lexemasIguales("TK_SI");
            }
            
            boolean esSalto() {
                return tokenActual.lexemasIguales("TK_IR_A");
            }
            
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="EXP">

            boolean esNumero() {
                return tokenActual.lexemasIguales("TK_CTE_NUM");
            }
        
            boolean esNumeroD() {
                return tokenActual.lexemasIguales("TK_NOTCNTF");
            }

            boolean esParentesisAbierto () {
                return tokenActual.lexemasIguales("TK_PAR_ABR");
            }

            boolean esParentesisCerrado () {
                return tokenActual.lexemasIguales("TK_PAR_CER");
            }
            
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="COLOR">
            
            boolean esNegro() {
                return tokenActual.lexemasIguales("TK_NEGRO");
            }

            boolean esVerde() {
                return tokenActual.lexemasIguales("TK_VERDE");
            }

            boolean esNaranja() {
                return tokenActual.lexemasIguales("TK_NARANJA");
            }

            boolean esRosa() {
                return tokenActual.lexemasIguales("TK_ROSA");
            }

            boolean esRojo() {
                return tokenActual.lexemasIguales("TK_ROJO");
            }

            boolean esBlanco() {
                return tokenActual.lexemasIguales("TK_BLANCO");
            }

            boolean esAmarillo() {
                return tokenActual.lexemasIguales("TK_AMARILLO");
            }

            boolean esMagenta() {
                return tokenActual.lexemasIguales("TK_MAGENTA");
            }
            
        //</editor-fold>
    
    //</editor-fold>      
}
