public class Pila {
    private Nod0 tope;
    private int tamanio;

    public Pila() {
        this.tope = null;
        this.tamanio = 0;
    }

    public void push(Token token) {
        Nod0 nuevo = new Nod0(token);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
    }

    public Token pop() {
        if (estaVacia()) {
            return null;
        }
        Token token = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return token;
    }

    public Token peek() {
        return estaVacia() ? null : tope.getDato();
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void vaciar() {
        tope = null;
        tamanio = 0;
    }

    public void mostrar() {
        if (estaVacia()) {
            System.out.println("   (la pila esta vacia)");
            return;
        }
        System.out.println("   +--------------------+");
        Nod0 actual = tope;
        while (actual != null) {
            String marca = (actual == tope) ? "  <- TOPE" : "";
            System.out.printf("   | %-18s |%s%n", actual.getDato(), marca);
            System.out.println("   +--------------------+");
            actual = actual.getSiguiente();
        }
    }

    @Override
    public String toString() {
        if (estaVacia()) {
            return "[ vacia ]";
        }
        StringBuilder sb = new StringBuilder("[ TOPE -> ");
        Nod0 actual = tope;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(", ");
            }
            actual = actual.getSiguiente();
        }
        return sb.append(" ]").toString();
    }
}
