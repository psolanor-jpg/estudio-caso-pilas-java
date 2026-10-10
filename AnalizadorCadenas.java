import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalizadorCadenas {
    public static boolean esIdentificadorValido(String nombre) {
        return nombre != null && nombre.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    public List<Token> tokenizar(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < entrada.length()) {
            char c = entrada.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
            } else if (c == '+') {
                tokens.add(new Token(TipoToken.OPERADOR, "+"));
                i++;
            } else if (c == '"') {
                int inicio = i;
                StringBuilder contenido = new StringBuilder();
                boolean cerrada = false;
                i++;
                while (i < entrada.length() && !cerrada) {
                    char actual = entrada.charAt(i);
                    if (actual == '\\' && i + 1 < entrada.length()
                            && (entrada.charAt(i + 1) == '"' || entrada.charAt(i + 1) == '\\')) {
                        contenido.append(entrada.charAt(i + 1));
                        i += 2;
                    } else if (actual == '"') {
                        cerrada = true;
                        i++;
                    } else {
                        contenido.append(actual);
                        i++;
                    }
                }
                if (!cerrada) {
                    throw new IllegalArgumentException(
                            "La cadena que empieza en la posicion " + (inicio + 1) + " no tiene comilla de cierre.");
                }
                tokens.add(new Token(TipoToken.LITERAL, contenido.toString()));
            } else if (Character.isLetter(c) || c == '_') {
                int inicio = i;
                while (i < entrada.length()
                        && (Character.isLetterOrDigit(entrada.charAt(i)) || entrada.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(new Token(TipoToken.VARIABLE, entrada.substring(inicio, i)));
            } else {
                throw new IllegalArgumentException(
                        "Caracter no valido '" + c + "' en la posicion " + (i + 1) + ".");
            }
        }

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("La cadena de impresion esta vacia.");
        }
        return tokens;
    }

    public void validar(List<Token> tokens) {
        boolean esperaOperando = true;

        for (int i = 0; i < tokens.size(); i++) {
            Token actual = tokens.get(i);

            if (esperaOperando && actual.esOperador()) {
                if (i == 0) {
                    throw new IllegalArgumentException("La cadena no puede empezar con el operador '+'.");
                }
                throw new IllegalArgumentException("Hay dos operadores '+' seguidos (token " + (i + 1) + ").");
            }
            if (!esperaOperando && actual.esOperando()) {
                throw new IllegalArgumentException("Falta el operador '+' entre "
                        + tokens.get(i - 1) + " y " + actual + ".");
            }
            esperaOperando = !esperaOperando;
        }

        if (esperaOperando) {
            throw new IllegalArgumentException("La cadena no puede terminar con el operador '+'.");
        }
    }

    public List<Token> aPosfija(List<Token> tokens, boolean mostrarPasos) {
        Pila operadores = new Pila();
        List<Token> salida = new ArrayList<>();

        for (Token token : tokens) {
            if (token.esOperando()) {
                salida.add(token);
                if (mostrarPasos) {
                    paso("Operando " + token + " -> va directo a la salida", operadores, "Salida: " + salida);
                }
            } else {
                while (!operadores.estaVacia()) {
                    Token anterior = operadores.pop();
                    salida.add(anterior);
                    if (mostrarPasos) {
                        paso("POP de " + anterior + " -> pasa a la salida", operadores, "Salida: " + salida);
                    }
                }
                operadores.push(token);
                if (mostrarPasos) {
                    paso("PUSH del operador " + token, operadores, "Salida: " + salida);
                }
            }
        }

        while (!operadores.estaVacia()) {
            Token restante = operadores.pop();
            salida.add(restante);
            if (mostrarPasos) {
                paso("Fin de la cadena: POP de " + restante + " -> pasa a la salida", operadores, "Salida: " + salida);
            }
        }
        return salida;
    }

    public String evaluar(List<Token> posfija, Map<String, String> variables, boolean mostrarPasos) {
        Pila operandos = new Pila();

        for (Token token : posfija) {
            if (token.esOperando()) {
                String valor = token.getTipo() == TipoToken.LITERAL
                        ? token.getValor()
                        : variables.get(token.getValor());
                if (valor == null) {
                    throw new IllegalArgumentException("La variable '" + token.getValor() + "' no esta definida.");
                }
                operandos.push(new Token(TipoToken.LITERAL, valor));
                if (mostrarPasos) {
                    paso("PUSH del operando " + token + " (valor \"" + valor + "\")", operandos, null);
                }
            } else {
                Token derecho = operandos.pop();
                Token izquierdo = operandos.pop();
                if (derecho == null || izquierdo == null) {
                    throw new IllegalStateException("Faltan operandos para el operador '+'.");
                }
                String unido = izquierdo.getValor() + derecho.getValor();
                operandos.push(new Token(TipoToken.LITERAL, unido));
                if (mostrarPasos) {
                    paso("Operador +: POP de " + derecho + " y " + izquierdo
                            + ", se concatenan y se hace PUSH de \"" + unido + "\"", operandos, null);
                }
            }
        }

        Token resultado = operandos.pop();
        if (resultado == null || !operandos.estaVacia()) {
            throw new IllegalStateException("La expresion esta mal formada.");
        }
        return resultado.getValor();
    }

    private void paso(String descripcion, Pila pila, String extra) {
        System.out.println("   - " + descripcion);
        System.out.println("       Pila: " + pila);
        if (extra != null) {
            System.out.println("       " + extra);
        }
    }
}
