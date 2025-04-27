package org.example.one_dashboard_multiple_pages;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ScientificController {
    @FXML
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonBack, buttonBracket, buttonBracketClose, buttonC, buttonCe, buttonCopyFirst, buttonCopyLast,
            buttonCos, buttonCosec, buttonCot, buttonDivide, buttonDot, buttonE, buttonEquals, buttonExponent,
            buttonFactorial, buttonLn, buttonLog, buttonMinus, buttonModX, buttonMultiply, buttonOnOff, buttonPie,
            buttonPlus, buttonSec, buttonSine, buttonCube, buttontan;

    @FXML
    private FlowPane flowPane;

    @FXML
    private TextArea historyTextField;

    @FXML
    private TextField textField;


    boolean updateMod = false;
    String value;
    public static int countForDot = 1;
    private final Set<KeyCode> activeKeys = new HashSet<>();

    private void setText(String value) {
        if (updateMod) {
            textField.setText("|" + textField.getText());
            updateMod = false;
        } else {
            textField.setText(textField.getText() + value);
        }
    }

    @FXML
    public void initialize() {
        // Set up keyboard event handlers
        flowPane.setOnKeyPressed(this::handleKeyPress);
        flowPane.setOnKeyReleased(this::handleKeyRelease);
        buttonCopyFirst.setDisable(true);

        // Prevent buttons from gaining focus
        disableButtonFocus();

        // Ensure textField always keeps focus
        Platform.runLater(() -> textField.requestFocus());

        // Listen for focus changes and bring it back to textField
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Platform.runLater(() -> textField.requestFocus());
                Platform.runLater(() -> textField.positionCaret(textField.getText().length()));
            }
        });
    }

    // Prevent buttons from receiving focus
    private void disableButtonFocus() {
        Button[] buttons = {
                button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
                buttonBack, buttonBracket, buttonBracketClose, buttonC, buttonCe, buttonCopyFirst, buttonCopyLast,
                buttonCos, buttonCosec, buttonCot, buttonDivide, buttonDot, buttonE, buttonEquals, buttonExponent,
                buttonFactorial, buttonLn, buttonLog, buttonMinus, buttonModX, buttonMultiply, buttonOnOff, buttonPie,
                buttonPlus, buttonSec, buttonSine, buttontan, buttonCube

        };
        for (Button button : buttons) {
            button.setFocusTraversable(false);
        }
    }

    // Ensure focus stays on textField after button clicks
    @FXML
    void getButtonValue(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();
        value = clickedButton.getText();
        if (clickedButton == buttonCopyFirst) {
            copyLastHistoryEntry();
        } else if (clickedButton == buttonCopyLast) {
            copyLastHistoryEntry2();
        } else {
            processInput(value);
        }

        // Restore focus to textField
        Platform.runLater(() -> textField.requestFocus());
        Platform.runLater(() -> textField.positionCaret(textField.getText().length()));

    }

    private void copyLastHistoryEntry2() {
        String history = historyTextField.getText();
        String[] lines = history.split("\n");
        if (lines.length > 0) {
            String lastLine = lines[lines.length - 1]; // Get the last history entry
            int equalsIndex = lastLine.indexOf("="); // Find the position of '='
            if (equalsIndex != -1 && equalsIndex + 1 < lastLine.length()) {
                String beforeEquals = lastLine.substring(0, equalsIndex).trim(); // Extract text after '='
                textField.clear();
                textField.appendText(beforeEquals);
                textField.requestFocus();
                Platform.runLater(() -> textField.deselect()); // **Explicitly deselect text**
                Platform.runLater(() -> textField.positionCaret(textField.getText().length())); // Move cursor to the end
            }
        }
    }

    private void copyLastHistoryEntry() {
        String history = historyTextField.getText();
        String[] lines = history.split("\n");
        if (lines.length > 0) {
            String lastLine = lines[lines.length - 1]; // Get the last history entry
            int equalsIndex = lastLine.indexOf("="); // Find the position of '='
            if (equalsIndex != -1 && equalsIndex + 1 < lastLine.length()) {
                String afterEquals = lastLine.substring(equalsIndex + 1).trim(); // Extract  text after '='
                textField.clear();
                textField.appendText(afterEquals);
                textField.requestFocus();
                Platform.runLater(() -> textField.deselect());  // **Explicitly deselect text**
                Platform.runLater(() -> textField.positionCaret(textField.getText().length())); // Move cursor to the end
            }
        }
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if (keyCode.isDigitKey() || keyCode == KeyCode.PERIOD || keyCode.isLetterKey()) {
            processInput(event.getText());
        } else if (keyCode == KeyCode.BACK_SPACE) {
            deleteLastCharacter();
        } else if (keyCode == KeyCode.ENTER) {
            evaluateExpressionAndUpdateUI(); // Calls method to evaluate the total
        } else if (keyCode == KeyCode.ESCAPE) {
            textField.clear();
            historyTextField.clear();
        }
        activeKeys.add(keyCode);
        event.consume();
    }

    private void evaluateExpressionAndUpdateUI() {
        String input = textField.getText();
        try {
            double result = evaluateExpression(input);
            historyTextField.appendText(input + " = " + result + "\n");
            textField.clear();
            buttonCopyFirst.setDisable(false);
            buttonCopyLast.setDisable(false);
        } catch (Exception e) {
            textField.setText("Error");
            textField.positionCaret(textField.getText().length()); // Move cursor to the end of the text
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        activeKeys.remove(event.getCode());
        event.consume();
    }

    private void processInput(String input) {
        switch (input) {

            case "Copy Value":
                copyLastHistoryEntry();
                break;
            case "Copy Expression":
                copyLastHistoryEntry2();
                break;
            case "C":
                textField.clear();
                break;
            case "CE":
                textField.clear();
                historyTextField.clear();
                break;
            case "e":
                setText("2.7182818284590452353602874713527");
                countForDot = 0;
                break;
            case "x^3":
                setText("^3");
                calculateCube();
                break;
            case "exp":
                setText("E");
                break;
            case "π":
                setText("3.1415926535897932384626433832795");
                countForDot = 0;
                break;
            case "|x|":
                setText("|");
                updateMod = true;
                setText("|");
                getMod();
                break;
            case "n!":
                setText("!");
                calculateFactorial();
                break;
            case "<-":
                deleteLastCharacter();
                break;

            case "x²":
                setText("²");
                break;
            case "OFF":
                setStatus(true);
                buttonOnOff.setText("ON");
                break;
            case "ON":
                setStatus(false);
                buttonOnOff.setText("OFF");
                break;
            default:
                if (input.matches("[0-9]")) {
                    setText(input);
                } else if (input.matches("[+\\-*/]")) {
                    setText(input);
                    countForDot = 1;
                } else if (".".equals(input)) {
                    if (countForDot == 1) {
                        setText(input);
                        countForDot = 0;
                    }
                } else if (input.matches("sin|cos|tan|sec|cosec|cot")) {
                    setText(input + "(");
                    countForDot = 1;
                } else if (")".equals(input)) {
                    setText(input);
                } else if ("ln e".equals(input)) {
                    countForDot = 1;
                    setText("ln(");
                } else if ("log 10".equals(input)) {
                    countForDot = 1;
                    setText("log(");
                }
                break;
        }
    }

    private void calculateCube() {
        String text = textField.getText().trim();
        try {
            int expIndex = text.indexOf('^');

            if (expIndex == -1) {
                textField.setText("Error: Missing '^' for exponentiation");
                return;
            }

            String baseStr = text.substring(0, expIndex).trim();
            String exponentStr = text.substring(expIndex + 1).trim();

            if (baseStr.isEmpty() || exponentStr.isEmpty()) {
                textField.setText("Error: Invalid exponentiation format");
                return;
            }

            double base = Double.parseDouble(baseStr);
            double exponent = Double.parseDouble(exponentStr);

            double result = Math.pow(base, exponent);
            textField.setText(String.valueOf(result));
            historyTextField.appendText(text + " = " + result + "\n");
        } catch (NumberFormatException e) {
            textField.setText("Error: Invalid numeric input for exponentiation");
        } catch (Exception e) {
            textField.setText("Unexpected error while calculating exponentiation");
        }
    }

    private void getMod() {
        String text = textField.getText();
        try {
            int start = text.indexOf('|');
            int end = text.lastIndexOf('|');

            if (start != -1 && end > start) {
                String innerText = text.substring(start + 1, end).trim();
                double value = Math.abs(Double.parseDouble(innerText));

                textField.setText(String.valueOf(value));
                historyTextField.setText(text + " = " + value);
            } else {
                textField.setText("Error: Invalid modulus format");
            }
        } catch (Exception ex) {
            textField.setText("Error: Unable to compute modulus");
        }
    }

    private void calculateFactorial() {
        String text = textField.getText();
        try {
            if (!text.endsWith("!")) {
                textField.setText("Error: Missing '!' for factorial");
                return;
            }

            int value = Integer.parseInt(text.substring(0, text.length() - 1).trim());
            if (value < 0) {
                textField.setText("Error: Factorial not defined for negative numbers");
                return;
            }

            double result = 1;
            for (int i = 1; i <= value; i++) {
                result *= i;
            }

            textField.setText(String.valueOf(result));
            historyTextField.setText(text + " = " + result);
        } catch (NumberFormatException e) {
            textField.setText("Error: Factorial must be a positive integer");
        }
    }

    public void setStatus(boolean trueorfalse) {
        Button[] buttons = {
                button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
                buttonBack, buttonBracket, buttonBracketClose, buttonC, buttonCe, buttonCopyFirst, buttonCopyLast,
                buttonCos, buttonCosec, buttonCot, buttonDivide, buttonDot, buttonE, buttonEquals, buttonExponent,
                buttonFactorial, buttonLn, buttonLog, buttonMinus, buttonModX, buttonMultiply, buttonPie,
                buttonPlus, buttonSec, buttonSine, buttonCube, buttontan
        };
        for (Button button : buttons) {
            button.setDisable(trueorfalse);
            textField.setDisable(trueorfalse);
            historyTextField.setDisable(trueorfalse);
        }

    }

    private void deleteLastCharacter() {
        String text = textField.getText();
        if (!text.isEmpty()) {
            textField.setText(text.substring(0, text.length() - 1));
        }
    }

    public static double evaluateExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            return 0;
        }
        String postfix = infixToPostfix(expression);
        return evaluatePostfix(postfix);
    }

    private static String infixToPostfix(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operators = new Stack<>();
        boolean lastWasOperator = true;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                output.append(c);
                lastWasOperator = false;
            } else if (c == '-' && (i == 0 || lastWasOperator)) {
                output.append(c);
                lastWasOperator = false;
            } else if (c == 'E' ) {
                output.append('E');
            } else if (Character.isLetter(c)) {
                StringBuilder func = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    func.append(expression.charAt(i));
                    i++;
                }
                output.append(" ");
                String functionName = func.toString();
                if (functionName.equals("log10")) functionName = "log";
                if (i < expression.length() && expression.charAt(i) == '(') {
                    i++;
                    StringBuilder number = new StringBuilder();
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        number.append(expression.charAt(i));
                        i++;
                    }
                    output.append(number).append(" ").append(functionName);
                    lastWasOperator = false;
                }
            } else if ("+-*/^".indexOf(c) != -1) {
                output.append(" ");
                while (!operators.isEmpty() && !operators.peek().equals("(") &&
                        precedence(operators.peek().charAt(0)) >= precedence(c)) {
                    output.append(operators.pop()).append(" ");
                }
                operators.push(String.valueOf(c));
                lastWasOperator = true;
            } else if (c == ')') {
                output.append(" ");
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.append(operators.pop()).append(" ");
                }
                operators.pop();
            }
        }

        while (!operators.isEmpty()) {
            output.append(" ").append(operators.pop());
        }
        return output.toString().trim();
    }

    private static double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (token.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (isFunction(token)) {
                double operand = stack.pop();
                switch (token) {
                    case "sin":
                        stack.push(Math.sin(Math.toRadians(operand)));
                        break;
                    case "cos":
                        stack.push(Math.cos(Math.toRadians(operand)));
                        break;
                    case "tan":
                        stack.push(Math.tan(Math.toRadians(operand)));
                        break;
                    case "sec":
                        stack.push(1 / Math.cos(Math.toRadians(operand)));
                        break;
                    case "cosec":
                        stack.push(1 / Math.sin(Math.toRadians(operand)));
                        break;
                    case "cot":
                        stack.push(Math.cos(Math.toRadians(operand)) / Math.sin(Math.toRadians(operand)));
                        break;
                    case "log":
                        stack.push(Math.log10(operand));
                        break;
                    case "ln":
                        stack.push(Math.log(operand));
                        break;
                    case "sqrt":
                        stack.push(Math.sqrt(operand));
                        break;
                }
            } else if (token.equals("²")) {
                double base = stack.pop();
                stack.push(Math.pow(base, 2));
            } else if (token.equals("^")) {
                double exponent = stack.pop();
                double base = stack.pop();
                stack.push(Math.pow(base, exponent));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token.charAt(0)) {
                    case '+':
                        stack.push(a + b);
                        break;
                    case '-':
                        stack.push(a - b);
                        break;
                    case '*':
                        stack.push(a * b);
                        break;
                    case '/':
                        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                        stack.push(a / b);
                        break;
                }
            }
        }
        return stack.pop();
    }

    private static int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private static boolean isFunction(String token) {
        return token.equals("sin") || token.equals("cos") || token.equals("tan") ||
                token.equals("log") || token.equals("ln") || token.equals("sec") ||
                token.equals("cosec") || token.equals("cot");
    }

    public void evaluate(MouseEvent mouseEvent) {
        value = textField.getText();
        try {
            double result = evaluateExpression(value);
            historyTextField.appendText(value + " = " + result + "\n");
            textField.clear();
            textField.setText(String.valueOf(result));
            buttonCopyFirst.setDisable(false);
            buttonCopyLast.setDisable(false);
        } catch (Exception e) {
            textField.setText("Error: Invalid Input");
        }
    }
}