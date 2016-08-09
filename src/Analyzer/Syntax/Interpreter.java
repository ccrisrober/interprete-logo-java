package Analyzer.Syntax;

import Dependencies.PR1.Symbols.VT;
import Dependencies.PR3.Token;
import Exceptions.Interpreter.InterpreterException;
import Exceptions.Syntax.Extends.ColorException;
import Exceptions.Syntax.Extends.NewLineException;
import Exceptions.Syntax.Extends.OperatorException;
import Exceptions.Syntax.SyntaxException;
import Interface.Turtle;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Interpreter {

    protected Token tokenActual;
    protected List<Token> listaTokens;
    protected int posicion;
    protected int nLinea;
    protected Turtle t;
    protected Map<Integer, Integer> mapaLineas;
    private final int MAX = 1000000;
    protected int nLlamadasRecursivas = MAX;
    protected Map<String, Double> tablaIdentificadores;
    protected String finFichero = "$EOF$";

    public Interpreter(Turtle t) {
        this.t = t;
        posicion = 0;
        nLinea = 0;
    }

    /**
     * Genera una tabla hash con clave: número de linea y valor: posición del
     * token.
     */
    private void generarMapaLineas() {
        mapaLineas.put(1, 1);
    }

    public void interpretar(List<Token> listaTks) {
        mapaLineas = new HashMap<Integer, Integer>();
        tablaIdentificadores = new HashMap<String, Double>();
        this.listaTokens = listaTks;
        nLinea = 0;
        posicion = 0;
        generarMapaLineas();
        do {
            if(nLlamadasRecursivas == 0) {
                throw new InterpreterException("Número de llamadas recursivas demasiado grande.");
            }
            nLinea++;
            try {
                leerSiguienteToken();
                mapaLineas.put(nLinea, posicion);
                PASEO();
            }
            catch (Exception e) {
                throw new InterpreterException("Línea " + nLinea + ": " + e.getMessage());
            }
            if (!tokenActual.lexemasIguales("TK_FIN_SENT") && !tokenActual.lexemasIguales("TK_SALIDA")) {
                throw new NewLineException("Encontrado '" + this.devolverErrorMatch(tokenActual.getLexema().substring(3)) + "', esperaba 'salto de línea'.");
            }
        }
        while (!tokenActual.lexemasIguales("TK_SALIDA"));
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
            t.home();
            return;
        }
        if (esGiro()) {
            Match(new VT("TK_GIRO"));
            double d = E();
            t.turn(d);
            return;
        }
        if (esAvanza()) {
            Match(new VT("TK_AVANZA"));
            double d = E();
            t.penUp();
            t.move(d);
            t.penDown();
            return;
        }
        if (esPinta()) {
            Match(new VT("TK_PINTA"));
            double d = E();
            t.forward(d);
            return;
        }
        if (esID()) {
            String nombreID = (String) tokenActual.getContenido();
            Match(new VT("TK_ID"));
            Match(new VT("TK_ASIGN"));
            double d = E();
            tablaIdentificadores.put(nombreID, d);
            return;
        }
        if (esColor()) {
            Match(new VT("TK_COLOR"));
            Match(new VT("TK_ASIGN"));
            Color c = COLOR();
            t.setColor(c);
            return;
        }
        if (esCondicional()) {
            Match(new VT("TK_SI"));
            boolean resultado = CONDICIONAL();
            if (resultado) {
                Match(new VT("TK_ENTONCES"));
                PASO();
                nLlamadasRecursivas--;
            }
            else {
                irSiguienteLinea();
            }
            return;
        }
        if (esSalto()) {
            Match(new VT("TK_IR_A"));
            int valor = (int) E();
            saltar(valor);
            return;
        }
        throw new SyntaxException("Esperaba pinta, casa, avanza, giro, identificador, color, condicional (SI) o salto (ir_a).");
    }

    void irSiguienteLinea() {
        boolean encontrado = false;
        while ((this.listaTokens.size() > posicion) && !encontrado) {
            if ((this.listaTokens.get(posicion).equals(new Token("TK_FIN_SENT", "$")))) {
                encontrado = true;
                leerSiguienteToken();
                break;
            }
            else {
                posicion++;
            }
        }
        if (!encontrado) {
            tokenActual = new Token("TK_SALIDA", finFichero);
        }
    }

    boolean CONDICIONAL() {
        boolean not = NOT(true);

        boolean salida;
        if (esParentesisAbierto()) {
            salida = COND_PAR();
        }
        else {
            salida = COMP();
        }
        return not && salida;
    }

    boolean COND_PAR() {
        boolean salida = true;
        if (esParentesisAbierto()) {
            Match(new VT("TK_PAR_ABR"));
            salida = CONDICIONAL();
            Match(new VT("TK_PAR_CER"));
            if (esAndOr()) {
                salida = AndOr(salida);
            }
        }
        return salida;
    }

    boolean NOT(boolean not) {
        if (esNot()) {
            Match(new VT("TK_NOT"));
            not = !NOT(not);
        }
        return not;
    }

    boolean AndOr(boolean r) {
        if (esAnd()) {
            Match(new VT("TK_AND"));
            boolean r2 = CONDICIONAL();
            return r && r2;
        }
        if (esOr()) {
            Match(new VT("TK_OR"));
            boolean r2 = CONDICIONAL();
            return r || r2;
        }
        throw new OperatorException("Esperaba AND o OR.");
    }

    boolean COMP() {
        double valor1 = E();
        if (esComparador()) {
            if (esMayor()) {
                Match(new VT("TK_MAYOR"));
                double valor2 = E();
                return valor1 > valor2;
            }
            else if (esMenor()) {
                Match(new VT("TK_MENOR"));
                double valor2 = E();
                return valor1 < valor2;
            }
            else if (esDistinto()) {
                Match(new VT("TK_DISTINTO"));
                double valor2 = E();
                return valor1 != valor2;
            }
            else if (esComparacion()) {
                Match(new VT("TK_IGUALDAD"));
                double valor2 = E();
                return valor1 == valor2;
            }
            else if (esMayorIgual()) {
                Match(new VT("TK_MAYOR_IGUAL"));
                double valor2 = E();
                return valor1 >= valor2;
            }
            else if (esMenorIgual()) {
                Match(new VT("TK_MENOR_IGUAL"));
                double valor2 = E();
                return valor1 <= valor2;
            }
        }
        throw new OperatorException("Esperaba comparador.");
    }

    Color COLOR() {
        if (esNegro()) {
            Match(new VT("TK_NEGRO"));
            return Color.black;
        }
        if (esVerde()) {
            Match(new VT("TK_VERDE"));
            return Color.green;
        }
        if (esNaranja()) {
            Match(new VT("TK_NARANJA"));
            return Color.orange;
        }
        if (esRosa()) {
            Match(new VT("TK_ROSA"));
            return Color.pink;
        }
        if (esRojo()) {
            Match(new VT("TK_ROJO"));
            return Color.red;
        }
        if (esBlanco()) {
            Match(new VT("TK_BLANCO"));
            return Color.white;
        }
        if (esAmarillo()) {
            Match(new VT("TK_AMARILLO"));
            return Color.yellow;
        }
        if (esMagenta()) {
            Match(new VT("TK_MAGENTA"));
            return Color.magenta;
        }
        throw new ColorException("Esperaba color.");
    }

    double E() {
        double valor = T();
        if (esSumaResta()) {
            valor = E_PRIMA(valor);
        }
        return valor;
    }

    double E_PRIMA(double valor) {
        if (esSuma()) {
            Match(new VT("TK_MAS"));
            double valor2 = T();
            valor += valor2;
            if (esSumaResta()) {
                valor = E_PRIMA(valor);
            }
            return valor;
        }
        if (esResta()) {
            Match(new VT("TK_MENOS"));
            double valor2 = T();
            valor -= valor2;
            if (esSumaResta()) {
                valor = E_PRIMA(valor);
            }
            return valor;
        }
        throw new ArithmeticException("Esperaba '+' o '-'.");
    }

    double T() {
        double valor = F();
        if (esMultiplicacionDivision()) {
            valor = T_PRIMA(valor);
        }
        return valor;
    }

    double T_PRIMA(double valor) {
        if (esMultiplicacion()) {
            Match(new VT("TK_PROD"));
            double valor2 = F();
            valor *= valor2;
            if (esMultiplicacionDivision()) {
                valor = T_PRIMA(valor);
            }
            return valor;
        }
        if (esDivision()) {
            Match(new VT("TK_DIV"));
            double valor2 = F();
            if (valor2 == 0) {
                throw new InterpreterException("División entre 0.");
            }
            valor /= valor2;
            if (esMultiplicacionDivision()) {
                valor = T_PRIMA(valor);
            }
            return valor;
        }
        throw new ArithmeticException("Esperaba '*' o '/'.");
    }

    double F() {
        if (esParentesisAbierto()) {
            Match(new VT("TK_PAR_ABR"));
            double valor = E();
            Match(new VT("TK_PAR_CER"));
            return valor;
        }
        else if (esID()) {
            double valor = devolverTabla((String) tokenActual.getContenido());
            Match(new VT("TK_ID"));
            return valor;
        }
        else if (esNumero()) {
            double valor = 0;
            try {
                if (tokenActual.getContenido() instanceof Integer) {
                    valor = (double) (int) tokenActual.getContenido();
                }
                else {  //Suponemos que es double entonces
                    valor = (double) tokenActual.getContenido();
                }
            }
            catch (Exception e) {
                throw new InterpreterException("No es número entero o double");
            }
            Match(new VT("TK_CTE_NUM"));
            return valor;
        }
        else if (esNumeroD()) {
            double valor = Double.parseDouble((String) tokenActual.getContenido());
            Match(new VT("TK_NOTCNTF"));
            return valor;
        }
        else if (esMasMenos()) {
            int simbolo = MAS_MENOS(1);
            double valor = F();
            return valor * simbolo;
        }
        throw new SyntaxException("Esperaba paréntesis abierto, identificador o número.");
    }

    int MAS_MENOS(int n) {
        if (esMas()) {
            Match(new VT("TK_MAS"));
            return +n;
        }
        else if (esMenos()) {
            Match(new VT("TK_MENOS"));
            return -n;
        }
        throw new ArithmeticException("Espera '+' o '-'.");
    }

    void Match(VT v) {
        if (tokenActual.lexemasIguales(v.getV())) {
            if (posicion != listaTokens.size() - 1) {
                leerSiguienteToken();
                return;
            }
            else {
                tokenActual = new Token("TK_SALIDA", finFichero);
                return;
            }
        }
        throw new InterpreterException("Esperaba " + "'" + devolverErrorMatch(v.getV().substring(3)) + "'" + ", encontrado " + "'" + devolverErrorMatch((String)tokenActual.getContenido()) + "'.");
    }

    String devolverErrorMatch(String s) {
        if (s.compareToIgnoreCase("$") == 0) {
            s = "Fín de línea";
        }
        else if (s.compareToIgnoreCase("PAR_CER") == 0) {
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
        else if (s.compareToIgnoreCase("ASIGN") == 0) {
            s = "=";
        }
        else if (s.compareToIgnoreCase(finFichero) == 0) {
            s = ".";
        }
        return s;
    }

    void leerSiguienteToken() {
        tokenActual = listaTokens.get(posicion);
        ++posicion;
    }

    //<editor-fold defaultstate="collapsed" desc="Comparaciones">
    
        //<editor-fold defaultstate="collapsed" desc="COMPARADORES">
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
        boolean esMasMenos() {
            return esMas() || esMenos();
        }

        boolean esMas() {
            return tokenActual.lexemasIguales("TK_MAS");
        }

        boolean esMenos() {
            return tokenActual.lexemasIguales("TK_MENOS");
        }

        boolean esSumaResta() {
            return esMasMenos();
        }

        boolean esMultiplicacionDivision() {
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
            return esAnd() || esOr();
        }

        boolean esNot() {
            return tokenActual.lexemasIguales("TK_NOT");
        }

        boolean esAnd() {
            return tokenActual.lexemasIguales("TK_AND");
        }

        boolean esOr() {
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

        boolean esParentesisAbierto() {
            return tokenActual.lexemasIguales("TK_PAR_ABR");
        }

        boolean esParentesisCerrado() {
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
    
    double devolverTabla(String nombreID) {
        Set<Map.Entry<String, Double>> entrySet = tablaIdentificadores.entrySet();
        for (Map.Entry<String, Double> entry : entrySet) {
            if (entry.getKey().compareTo(nombreID) == 0) {
                return entry.getValue();
            }
        }
        throw new InterpreterException("Identificador '" + nombreID + "' no encontrado.");
    }

    double valorID(String nombreID) {
        Set<Entry<String, Double>> entrySet = tablaIdentificadores.entrySet();
        for (Entry<String, Double> entry : entrySet) {
            if (entry.getKey().compareTo(nombreID) == 0) {
                return entry.getValue();
            }
        }
        throw new InterpreterException("Identificador no encontrado.");
    }

    void saltar(int linea) {

        if (existeLinea(linea)) {
            Set<Entry<Integer, Integer>> entrySet = mapaLineas.entrySet();
            if (nLinea == linea) {
                throw new InterpreterException("Referencia a la misma línea.");
            }

            boolean encontrado = false;

            int numL = 0;
            for (Entry<Integer, Integer> entry : entrySet) {
                if (entry.getKey() == linea) {
                    posicion = entry.getValue() - 1;
                    //encontrado = true;
                    nLinea = numL;
                    tokenActual = listaTokens.get(posicion - 1);
                    return;
                }
                numL = numL + 1;
            }

            if (!encontrado) {
                throw new InterpreterException("Línea no encontrada.");
            }
        }
        throw new InterpreterException("Línea no encontrada.");
    }

    boolean existeLinea(int numLinea) {
        int min = this.nLinea;
        int aux = posicion;
        try {
            while (min < numLinea) {
                if (listaTokens.get(aux).lexemasIguales("TK_FIN_SENT")) {
                    min++;
                    if (aux + 1 <= listaTokens.size()) {
                        mapaLineas.put(min, aux - 1);
                    }
                }
                aux = aux + 1;
            }
            return mapaLineas.size() >= nLinea;
        }
        catch (Exception e) {
            return false;
        }
    }
}
