package Analyzer.Lexical;

import Dependencies.PR3.Token;
import Exceptions.Lexical.LexicalException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    protected final static String E = "\\+|\\-";
    protected final static String L = "[a-zA-ZñÑ]*";
    protected final static String N = "\\d*";
    protected final static String del = "\\s|\t";
    protected final static String del1 = "(\\s|\t)|\\(|\\{|\\=|\\+|\\-|\\*|\\/|\\$|\\)|\\<|\\>";
    protected final static String del2 = "(\\s|\t)|\\(|[a-zA-ZñÑ]|\\d";
    protected int puntero;
    protected List<Token> listaTokens;
    protected Set<String> palabrasReservadas;
    protected String lineaCodigo;
    protected String tokenActual; //Va almacenando los posibles tokens.
    protected String tokenLectura;
    protected final static String fin = "$#$";

    public LexicalAnalyzer(String... plbrReservadas) {
        listaTokens = new ArrayList<Token>();
        palabrasReservadas = new HashSet<String>();
        meterPalabrasReservadas(plbrReservadas);
    }

    public List<Token> analizarLinea(String linea) {
        IniLexema();
        lineaCodigo = linea;
        int estado = 0, valor = 0, digito;
        puntero = -1;
        while (!fin.equals(tokenLectura)) {
            switch (estado) {
                case 0: {
                    // Acciones semánticas del estado 0
                    IniLexema();
                    valor = 0;

                    tokenLectura = LeerSiguienteCaracter();

                    // Transiciones desde el estado 0
                    if (Pattern.compile(del).matcher(tokenLectura).matches()) {
                        estado = 0;
                    }
                    else if (Pattern.compile(L).matcher(tokenLectura).matches()) {
                        estado = 1;
                    }
                    else if (tokenLectura.compareTo("{") == 0) {
                        estado = 5;
                    }
                    else if (tokenLectura.compareTo("}") == 0) {
                        estado = 6;
                    }
                    else if (tokenLectura.compareTo("$") == 0) {
                        estado = 9;
                    }
                    else if (tokenLectura.compareTo("(") == 0) {
                        estado = 14;
                    }
                    else if (tokenLectura.compareTo(")") == 0) {
                        estado = 15;
                    }
                    else if (tokenLectura.compareTo("=") == 0) {
                        estado = 7;
                    }
                    else if (tokenLectura.compareTo("+") == 0) {
                        estado = 8;
                    }
                    else if (tokenLectura.compareTo("<") == 0) {
                        estado = 10;
                    }
                    else if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 16;
                    }
                    else if (tokenLectura.compareTo("/") == 0) {
                        estado = 24;
                    }
                    else if (tokenLectura.compareTo("-") == 0) {
                        estado = 30;
                    }
                    else if (tokenLectura.compareTo("*") == 0) {
                        estado = 32;
                    }
                    else if (tokenLectura.compareTo(">") == 0) {
                        estado = 35;
                    }
                    break;
                }
                case 1: {
                    // Acciones semánticas del estado 1 
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 1
                    if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 3;
                    }
                    else if (Pattern.compile(L).matcher(tokenLectura).matches()) {
                        estado = 1;
                    }
                    else if (Pattern.compile(del1).matcher(tokenLectura).matches()) {
                        estado = 2;
                    }
                    break;
                }
                case 2: {
                    // Acciones semánticas del estado 2 
                    RetrocesoPuntero();
                    DiferPRId(DaLexema());
                    // Transiciones desde el estado 2
                    estado = 0;
                    break;
                }
                case 3: {
                    // Acciones semánticas del estado 3
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 3
                    if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 3;
                    }
                    else if (Pattern.compile(L).matcher(tokenLectura).matches()) {
                        estado = 3;
                    }
                    else if (Pattern.compile(del1).matcher(tokenLectura).matches()) {
                        estado = 4;
                    }
                    break;
                }
                case 4: {
                    // Acciones semánticas del estado 4 
                    RetrocesoPuntero();
                    DaToken("TK_ID", DaLexema());
                    // Transiciones desde el estado 4 
                    estado = 0;
                    break;
                }
                case 5: {
                    // Acciones semánticas del estado 5 
                    DaToken("TK_LLAV_ABR", "{");
                    // Transiciones desde el estado 5 
                    estado = 0;
                    break;
                }
                case 6: {
                    // Acciones semánticas del estado 6 
                    DaToken("TK_LLAV_CER", "}");
                    // Transiciones desde el estado 6 
                    estado = 0;
                    break;
                }
                case 7: {
                    // Acciones semánticas del estado 7 
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 7 
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 11;
                    }
                    else if (tokenLectura.compareTo("=") == 0) {
                        estado = 39;
                    }
                    break;
                }
                case 8: {
                    // Acciones semánticas del estado 8 
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 8 
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 12;
                    }
                    break;
                }
                case 9: {
                    // Acciones semánticas del estado 9 
                    DaToken("TK_FIN_SENT", /*";"*/ "$");
                    // Transiciones desde el estado 9 
                    estado = 0;
                    break;
                }
                case 10: {
                    // Acciones semánticas del estado 10
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 10
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 13;
                    }
                    else if (tokenLectura.compareTo(">") == 0) {
                        estado = 37;
                    }
                    else if (tokenLectura.compareTo("=") == 0) {
                        estado = 42;
                    }
                    break;
                }
                case 11: {
                    // Acciones semánticas del estado 11 
                    RetrocesoPuntero();
                    DaToken("TK_ASIGN", "=");
                    // Transiciones desde el estado 11 
                    estado = 0;
                    break;
                }
                case 12: {
                    // Acciones semánticas del estado 12 
                    RetrocesoPuntero();
                    DaToken("TK_MAS", "+");
                    // Transiciones desde el estado 12 
                    estado = 0;
                    break;
                }
                case 13: {
                    // Acciones semánticas del estado 13 
                    RetrocesoPuntero();
                    DaToken("TK_MENOR", "<");
                    // Transiciones desde el estado 13 
                    estado = 0;
                    break;
                }
                case 14: {
                    // Acciones semánticas del estado 14 
                    DaToken("TK_PAR_ABR", "(");
                    // Transiciones desde el estado 14 
                    estado = 0;
                    break;
                }
                case 15: {
                    // Acciones semánticas del estado 15 
                    DaToken("TK_PAR_CER", ")");
                    // Transiciones desde el estado 15 
                    estado = 0;
                    break;
                }
                case 16: {
                    // Acciones semánticas del estado 16
                    digito = ConvierteNumero(tokenLectura);
                    valor = valor * 10 + digito;
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 16 
                    if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 16;
                    }
                    else if (Pattern.compile(del1).matcher(tokenLectura).matches()) {
                        estado = 17;
                    }
                    else if (tokenLectura.compareTo(".") == 0) {
                        estado = 18;
                        tokenLectura = valor + tokenLectura;
                    }
                    break;
                }
                case 17: {
                    // Acciones semánticas del estado 17
                    RetrocesoPuntero();
                    DaToken("TK_CTE_NUM", valor);
                    // Transiciones desde el estado 17
                    estado = 0;
                    break;
                }
                // NOTACIÓN CIENTÍFICA 

                case 18: {
                    // Acciones semánticas del estado 18 
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 18 
                    if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 18;
                    }
                    else if (Pattern.compile(E).matcher(tokenLectura).matches()) {	// + o -
                        estado = 19;
                    }
                    else if (tokenLectura.compareToIgnoreCase("E") == 0) {
                        estado = 20;
                    }
                    else if (Pattern.compile(del1).matcher(tokenLectura).matches()) {
                        estado = 22;
                    }
                    break;
                }
                case 19: {
                    // Acciones semánticas del estado 19
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 19
                    if (tokenLectura.compareToIgnoreCase("E") == 0) {
                        estado = 20;
                    }
                    else if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 19;
                    }
                    break;
                }
                case 20: {
                    // Acciones semánticas del estado 20 
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 20
                    if ((tokenLectura.compareTo("+") == 0) || (tokenLectura.compareTo("-") == 0) || (Pattern.compile(N).matcher(tokenLectura)).matches()) {
                        estado = 21;
                    }
                    break;
                }
                case 21: {
                    // Acciones semánticas del estado 21 
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 21 
                    if (Pattern.compile(del1).matcher(tokenLectura).matches()) {
                        estado = 22;
                        break;
                    }
                    else if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 21;
                    }
                    break;
                }
                case 22: {
                    // Acciones desde el estado 22
                    RetrocesoPuntero();
                    DaToken("TK_NOTCNTF", tokenActual);
                    // Transiciones desde el estado 22
                    estado = 0;
                    break;
                }


                // COMENTARIOS EN C

                case 24: {
                    // Acciones semánticas del estado 18 
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 18 
                    if (tokenLectura.compareTo("*") == 0) {
                        estado = 25;
                    }
                    else if (tokenLectura.compareTo("/") == 0) {
                        estado = 29;
                    }
                    else {  //¿ES DIVISIÓN?
                        estado = 34;
                    }
                    break;
                }
                case 25: {
                    // Acciones semánticas del estado 19 
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 19 
                    if (Pattern.compile(N).matcher(tokenLectura).matches()) {
                        estado = 25;
                    }
                    else if (Pattern.compile(L).matcher(tokenLectura).matches()) {
                        estado = 25;
                    }
                    else if (tokenLectura.compareTo("*") == 0) {
                        estado = 26;
                    }
                    break;
                }
                case 26: {
                    // Acciones semánticas del estado 26
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 26 
                    if (tokenLectura.compareTo("/") == 0) {
                        estado = 27;
                    }
                    break;
                }
                case 27: {
                    //Acciones semánticas del estado 27
                    ConcatenarCaracter(tokenLectura);
                    //DaToken("TK_COMENTARIO", tokenActual);
                    //Transiciones semánticas del estado 27
                    estado = 0;
                    break;
                }
                case 29: {
                    //Acciones semánticas del estado 29
                    ConcatenarCaracter(tokenLectura);
                    tokenLectura = LeerSiguienteCaracter();
                    if (tokenLectura.compareTo(fin) == 0) {
                        //DaToken("TK_COMENTARIO", tokenActual);
                    }
                    //Transiciones desde el estado 29
                    //estado = 29; //Creo que no hace falta
                    break;
                }
                case 30: {
                    // Acciones semánticas del estado 30 
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 30 
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 31;
                    }
                    break;
                }
                case 31: {
                    // Acciones semánticas del estado 31 
                    RetrocesoPuntero();
                    DaToken("TK_MENOS", "-");
                    // Transiciones desde el estado 31 
                    estado = 0;
                    break;
                }
                case 32: {
                    // Acciones semánticas del estado 32
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 32 
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 33;
                    }
                    break;
                }
                case 33: {
                    // Acciones semánticas del estado 33 
                    RetrocesoPuntero();
                    DaToken("TK_PROD", "*");
                    // Transiciones desde el estado 33
                    estado = 0;
                    break;
                }
                case 34: {
                    // Acciones semánticas del estado 34 
                    RetrocesoPuntero();
                    DaToken("TK_DIV", "/");
                    // Transiciones desde el estado 34
                    estado = 0;
                    break;
                }
                case 35: {
                    // Acciones semánticas del estado 35
                    tokenLectura = LeerSiguienteCaracter();
                    // Transicioens desde el estado 35
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 36;
                    }
                    else if (tokenLectura.compareTo("=") == 0) {
                        estado = 41;
                    }
                    break;
                }
                case 36: {
                    // Acciones semánticas del estado 36
                    RetrocesoPuntero();
                    DaToken("TK_MAYOR", ">");
                    // Transiciones desde el estado 36
                    estado = 0;
                    break;
                }
                case 37: {
                    // Acciones semánticas del estado 37
                    tokenLectura = LeerSiguienteCaracter();
                    // Transicioens desde el estado 37
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 38;
                    }
                    break;
                }
                case 38: {
                    // Acciones semánticas del estado 38
                    RetrocesoPuntero();
                    DaToken("TK_DISTINTO", "<>");
                    // Transiciones desde el estado 38
                    estado = 0;
                    break;
                }
                case 39: {
                    // Acciones semánticas del estado 39
                    tokenLectura = LeerSiguienteCaracter();
                    // Transiciones desde el estado 39
                    if (Pattern.compile(del2).matcher(tokenLectura).matches()) {
                        estado = 40;
                    }
                }
                case 40: {
                    // Acciones semánticas del estado 40 
                    RetrocesoPuntero();
                    DaToken("TK_IGUALDAD", "==");
                    // Transiciones desde el estado 40
                    estado = 0;
                    break;
                }
                case 41: {
                    // Acciones semánticas del estado 41
                    ConcatenarCaracter(tokenLectura);
                    //RetrocesoPuntero();
                    DaToken("TK_MAYOR_IGUAL", ">=");
                    estado = 0;
                    break;
                }
                case 42: {
                    // Acciones semánticas del estado 42
                    ConcatenarCaracter(tokenLectura);
                    //RetrocesoPuntero();
                    DaToken("TK_MENOR_IGUAL", "<=");
                    estado = 0;
                    break;
                }
            }//END switch.
        }//END while.
        listaTokens.add(new Token("TK_SALIDA", "$EOF$")); //Añado el fin de fichero
        for(Token tt: listaTokens){
            System.out.println(tt);
        }
        return listaTokens;
    }

    private void IniLexema() {
        tokenActual = tokenLectura = "";
    }

    private void RetrocesoPuntero() {
        puntero--;
    }

    private Token DaToken(String lexema, String contenido) {
        Token t = new Token(lexema, contenido);
        listaTokens.add(t);
        return t;
    }

    private Token DaToken(String lexema, int valor) {
        Token t = new Token(lexema, valor);
        listaTokens.add(t);
        return t;
    }

    /**
     * Diferencia si el lexema es una palabra reservada o un identificador
     *
     * @param lexema
     * @return true si es palabra reservada, false si es un identificador
     */
    private void DiferPRId(String lexema) {
        listaTokens.add(palabrasReservadas.contains(lexema) ? new Token("TK_" + DaLexema().toUpperCase(), lexema) : new Token("TK_ID", lexema));
    }

    private String DaLexema() {
        return tokenActual;
    }
    
    private void meterPalabrasReservadas(String... s) {
        palabrasReservadas.addAll(Arrays.asList(s));
    }

    private String LeerSiguienteCaracter() {
        puntero++;
        try {
            return lineaCodigo.charAt(puntero) + "";
        }
        catch (Exception e) {
            return fin;
        }
    }

    private void ConcatenarCaracter(String tokenLectura) {
        tokenActual += tokenLectura;
    }

    private int ConvierteNumero(String cadena) {
        int i = 0;
        try {
            i = Integer.parseInt(cadena);
        }
        catch (NumberFormatException e) {
            throw new LexicalException("Error al parsear numero");
        }
        return i;
    }
}