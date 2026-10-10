public class Token {
    private final TipoToken tipo;
    private final String valor;

    public Token(TipoToken tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public boolean esOperando() {
        return tipo == TipoToken.LITERAL || tipo == TipoToken.VARIABLE;
    }

    public boolean esOperador() {
        return tipo == TipoToken.OPERADOR;
    }

    @Override
    public String toString() {
        return tipo == TipoToken.LITERAL ? "\"" + valor + "\"" : valor;
    }
}
