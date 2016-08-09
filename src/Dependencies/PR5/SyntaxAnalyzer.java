package Dependencies.PR5;

import Dependencies.PR1.Grammar;
import Dependencies.PR1.Symbols.VT;
import java.util.Collection;

public abstract class SyntaxAnalyzer {

    protected Grammar g;

    public SyntaxAnalyzer(Grammar g) {
        this.g = g;
    }

    public abstract boolean analizar(Collection<VT> cV);
}