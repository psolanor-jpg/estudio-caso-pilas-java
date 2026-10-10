import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final AnalizadorCadenas analizador;
    private final Map<String, String> variables;
    private final Pila pilaManual;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.analizador = new AnalizadorCadenas();
        this.variables = new HashMap<>();
        this.pilaManual = new Pila();
    }

    public void iniciar() {
        try (scanner) {
            int opcion;
            do {
                System.out.println("\n===== ANALIZADOR DE CADENAS DE IMPRESION (PILA) =====");
                System.out.println("1. Definir una variable de cadena");
                System.out.println("2. Ver variables definidas");
                System.out.println("3. Analizar y evaluar una cadena de impresion");
                System.out.println("4. Operar la pila manualmente");
                System.out.println("5. Cargar ejemplo de demostracion");
                System.out.println("0. Salir");
                System.out.print("Elija una opcion: ");
                opcion = leerEntero();

                switch (opcion) {
                    case 1 -> definirVariable();
                    case 2 -> verVariables();
                    case 3 -> analizarCadena();
                    case 4 -> menuPila();
                    case 5 -> cargarEjemplo();
                    case 0 -> System.out.println("Saliendo del programa...");
                    default -> System.out.println("Opcion invalida.");
                }
            } while (opcion != 0);
        }
    }

    private void definirVariable() {
        System.out.print("Nombre de la variable: ");
        String nombre = scanner.nextLine().trim();
        if (!AnalizadorCadenas.esIdentificadorValido(nombre)) {
            System.out.println("Nombre invalido. Use letras, numeros o _ y no empiece con un numero.");
            return;
        }
        System.out.print("Valor (texto sin comillas): ");
        String valor = scanner.nextLine();
        variables.put(nombre, valor);
        System.out.println("Variable guardada: " + nombre + " = \"" + valor + "\"");
    }

    private void verVariables() {
        if (variables.isEmpty()) {
            System.out.println("No hay variables definidas todavia.");
            return;
        }
        System.out.println("Variables definidas:");
        variables.forEach((nombre, valor) -> System.out.println("   " + nombre + " = \"" + valor + "\""));
    }

    private void analizarCadena() {
        System.out.println("Escriba la cadena de impresion. Ejemplo: \"Hola, \" + nombre + \"!\"");
        System.out.print("> ");
        String entrada = scanner.nextLine();
        System.out.print("Mostrar el paso a paso de la pila? (s/n): ");
        boolean mostrarPasos = scanner.nextLine().trim().equalsIgnoreCase("s");
        ejecutarAnalisis(entrada, mostrarPasos);
    }

    private void ejecutarAnalisis(String entrada, boolean mostrarPasos) {
        try {
            List<Token> tokens = analizador.tokenizar(entrada);
            System.out.println("\n[1] Tokens:  " + tokens);

            analizador.validar(tokens);
            System.out.println("[2] Estructura valida.");

            System.out.println("[3] Conversion a posfija (pila de operadores):");
            List<Token> posfija = analizador.aPosfija(tokens, mostrarPasos);
            System.out.println("    Expresion posfija: " + posfija);

            System.out.println("[4] Evaluacion (pila de operandos):");
            String resultado = analizador.evaluar(posfija, variables, mostrarPasos);

            System.out.println("\nSalida impresa: " + resultado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nERROR: " + e.getMessage());
        }
    }

    private void cargarEjemplo() {
        variables.put("nombre", "Paulo");
        System.out.println("Se definio la variable: nombre = \"Paulo\"");
        System.out.println("Cadena de ejemplo: \"Hola, \" + nombre + \"!\"");
        ejecutarAnalisis("\"Hola, \" + nombre + \"!\"", true);
    }

    private void menuPila() {
        int opcion;
        do {
            System.out.println("\n----- OPERAR LA PILA MANUALMENTE -----");
            System.out.println("1. Push (apilar un token)");
            System.out.println("2. Pop (desapilar el tope)");
            System.out.println("3. Peek (ver el tope)");
            System.out.println("4. Ver la pila completa");
            System.out.println("5. Ver tamano de la pila");
            System.out.println("6. Vaciar la pila");
            System.out.println("0. Volver");
            System.out.print("Elija una opcion: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> apilarToken();
                case 2 -> desapilarToken();
                case 3 -> verTope();
                case 4 -> pilaManual.mostrar();
                case 5 -> System.out.println("Tamano: " + pilaManual.getTamanio());
                case 6 -> {
                    pilaManual.vaciar();
                    System.out.println("La pila fue vaciada.");
                }
                case 0 -> System.out.println("Volviendo al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void apilarToken() {
        System.out.println("Tipo de token: 1. Literal   2. Variable   3. Operador (+)");
        System.out.print("Elija el tipo: ");
        int tipo = leerEntero();

        Token token;
        switch (tipo) {
            case 1 -> {
                System.out.print("Texto del literal (sin comillas): ");
                token = new Token(TipoToken.LITERAL, scanner.nextLine());
            }
            case 2 -> {
                System.out.print("Nombre de la variable: ");
                String nombre = scanner.nextLine().trim();
                if (!AnalizadorCadenas.esIdentificadorValido(nombre)) {
                    System.out.println("Nombre de variable invalido. No se apilo nada.");
                    return;
                }
                token = new Token(TipoToken.VARIABLE, nombre);
            }
            case 3 -> token = new Token(TipoToken.OPERADOR, "+");
            default -> {
                System.out.println("Tipo invalido. No se apilo nada.");
                return;
            }
        }
        pilaManual.push(token);
        System.out.println("PUSH realizado: " + token);
    }

    private void desapilarToken() {
        Token token = pilaManual.pop();
        if (token == null) {
            System.out.println("La pila esta vacia: no hay nada que desapilar.");
        } else {
            System.out.println("POP realizado: " + token);
        }
    }

    private void verTope() {
        Token token = pilaManual.peek();
        if (token == null) {
            System.out.println("La pila esta vacia.");
        } else {
            System.out.println("Tope de la pila: " + token);
        }
    }

    private int leerEntero() {
        while (true) {
            String linea = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un numero valido: ");
            }
        }
    }
}
