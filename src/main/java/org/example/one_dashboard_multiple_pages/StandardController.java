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

public class StandardController {

    @FXML
    private Button button7, buttonMC, buttonMMinus, buttonMPlus, button0, button1, button2, button3, buttonCubeRoot, buttonPlusMinus, buttonSquareRoot, button4, button5, buttonMR, buttonMS, buttonModulo, button6, button8, button9, buttonBack, buttonC, buttonCe, buttonDivide, buttonDot, buttonEquals, buttonMinus, buttonMultiply, buttonOnOff, buttonPlus, buttonCopyFirst, buttonCopyLast, buttonSquare;
    @FXML
    private TextField textField;
    @FXML
    private TextArea historyTextField;
    @FXML
    private FlowPane flowPane;
    String value;
    public static int countForDot = 1;
    private double memory = 0.0;
    private final Set<KeyCode> activeKeys = new HashSet<>();
    boolean setCubeRoot = false, setSquareRoot = false;

    private void setText(String value) {
        textField.setText(textField.getText() + value);
        textField.positionCaret(textField.getText().length());
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

    private void disableButtonFocus() {
        Button[] buttons = {
                button7, buttonMC, buttonMMinus, buttonMPlus, button0, button1, button2,
                button3, buttonCubeRoot, buttonPlusMinus, buttonSquareRoot,
                button4, button5, buttonMR, buttonMS, buttonModulo,
                button6, button8, button9, buttonBack, buttonC, buttonCe, buttonDivide, buttonDot, buttonEquals, buttonMinus, buttonMultiply, buttonPlus, buttonCopyFirst, buttonCopyLast, buttonSquare

        };
        for (Button button : buttons) {
            button.setFocusTraversable(false);
        }
    }

    @FXML
    void getButtonValue(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();
        processInput(clickedButton.getText());
        Platform.runLater(() -> textField.requestFocus());
    }

    private void processInput(String input) {
        switch (input) {
            case "M+":
                if (!textField.getText().isEmpty()) {
                    memory += Double.parseDouble(textField.getText());
                }
                break;
            case "M-":
                if (!textField.getText().isEmpty()) {
                    memory -= Double.parseDouble(textField.getText());
                }
                break;
            case "MC":
                memory = 0.0;
                break;
            case "MR":
                textField.setText(String.valueOf(memory));
                break;
            case "MS":
                if (!textField.getText().isEmpty()) {
                    memory = Double.parseDouble(textField.getText());
                }
                break;
            case "%":
                applyPercentage();
                break;
            case "+/-":
                togglePlusMinus();
                break;
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
            case "<-":
                deleteLastCharacter();
                break;
            case "x²":
                setText("²");
                break;
            case "√":
                insertSquareRoot();
                break;
            case "∛":
                insertCubeRoot();
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
                if (input.matches("[0-9]") || input.matches("[+\\-*/]")) {
                    setText(input);
                    if (input.matches("[+\\-*/]")) {
                        countForDot = 1;
                    }
                } else if (".".equals(input)) {
                    if (countForDot == 1) {
                        setText(input);
                        countForDot = 0;
                    }
                }
                break;
        }
    }

    private void applyPercentage() {
        try {
            double value = Double.parseDouble(textField.getText());
            textField.setText(String.valueOf(value / 100));
        } catch (NumberFormatException e) {
            textField.setText("Error");
        }
    }

    private void togglePlusMinus() {
        try {
            double value = Double.parseDouble(textField.getText());
            textField.setText(String.valueOf(-value));
            textField.positionCaret(textField.getText().length());
        } catch (NumberFormatException e) {
            textField.setText("Error");
        }

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

            // Format result to avoid unnecessary decimal places
            String formattedResult = String.format("%.10f", result).replaceAll("\\.?0+$", "");

            historyTextField.appendText(input + " = " + formattedResult + "\n");
            textField.setText(formattedResult);  // Set formatted result in textField
            textField.positionCaret(textField.getText().length());  // Move cursor to end
            buttonCopyFirst.setDisable(false);
            buttonCopyLast.setDisable(false);
        } catch (ArithmeticException ae) {
            textField.setText("Math Error: " + ae.getMessage());  // Show Math Error
        } catch (Exception e) {
            textField.setText("Syntax Error: " + e.getMessage()); // Show Syntax Error
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        activeKeys.remove(event.getCode());
        event.consume();
    }

    private void insertCubeRoot() {

        String text = textField.getText(), text2 = "";
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            text2 = text2 + ch;
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                String text3 = text2.substring(0, i) + ch + "∛" + text.substring(i + 1);
                textField.setText(text3);
                textField.positionCaret(textField.getText().length());
                setCubeRoot = true;
            }
        }
        if (!setCubeRoot) {
            textField.setText("∛" + text);
            textField.positionCaret(textField.getText().length());
        }
    }

    private void insertSquareRoot() {
        String text = textField.getText(), text2 = "";
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            text2 = text2 + ch;
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                String text3 = text2.substring(0, i) + ch + "√" + text.substring(i + 1);
                textField.setText(text3);
                textField.positionCaret(textField.getText().length());
                setSquareRoot = true;
            }
        }
        if (!setSquareRoot) {
            textField.setText("√" + text);
            textField.positionCaret(textField.getText().length());
        }
    }

    public void setStatus(boolean trueorfalse) {
        Button[] buttons = {
                button7, buttonMC, buttonMMinus, buttonMPlus, button0, button1, button2, button3, buttonCubeRoot, buttonPlusMinus, buttonSquareRoot, button4, button5, buttonMR, buttonMS, buttonModulo, button6, button8, button9, buttonBack, buttonC, buttonCe, buttonDivide, buttonDot, buttonEquals, buttonMinus, buttonMultiply, buttonPlus, buttonCopyFirst, buttonCopyLast, buttonSquare};
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

    private static int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        if (op == '√' || op == '∛') return 3; // Higher precedence than * and /
        return 0;

    }

    private static String infixToPostfix(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operators = new Stack<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (numberBuffer.length() > 0) {
                    output.append(numberBuffer).append(" ");
                    numberBuffer.setLength(0);
                }

                if (c == '√' || c == '∛') {
                    operators.push(String.valueOf(c));  // Push root operators
                } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                    while (!operators.isEmpty() && precedence(operators.peek().charAt(0)) >= precedence(c)) {
                        output.append(operators.pop()).append(" ");
                    }
                    operators.push(String.valueOf(c));
                } else if (c == '(') {
                    operators.push("(");
                } else if (c == ')') {
                    while (!operators.isEmpty() && !operators.peek().equals("(")) {
                        output.append(operators.pop()).append(" ");
                    }
                    operators.pop();  // Remove '('
                }
            }
        }

        if (numberBuffer.length() > 0) {
            output.append(numberBuffer).append(" ");
        }

        while (!operators.isEmpty()) {
            output.append(operators.pop()).append(" ");
        }

        return output.toString().trim();
    }

    private static double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (token.matches("-?\\d+(\\.\\d+)?")) {  // If token is a number
                stack.push(Double.parseDouble(token));
            } else if (token.equals("√")) {
                if (stack.isEmpty())
                    throw new IllegalArgumentException("Invalid square root operation.");
                double num = stack.pop();
                if (num < 0)
                    throw new ArithmeticException("Cannot take square root of negative number.");
                stack.push(Math.sqrt(num));
            } else if (token.equals("∛")) {
                if (stack.isEmpty())
                    throw new IllegalArgumentException("Invalid cube root operation.");
                double num = stack.pop();
                stack.push(Math.cbrt(num));  // Cube root works for negative numbers too
            } else {  // Standard operators (+, -, *, /)
                if (stack.size() < 2)
                    throw new IllegalArgumentException("Invalid expression.");
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

    public void evaluate(MouseEvent mouseEvent) {
        value = textField.getText();
        try {
            double result = evaluateExpression(value);

            historyTextField.appendText(value + " = " + result + "\n");
            textField.setText(String.valueOf(result));
            buttonCopyFirst.setDisable(false);
            buttonCopyLast.setDisable(false);
            textField.positionCaret(textField.getText().length());
        } catch (Exception e) {
            textField.setText("Error: Invalid Input");
        }
    }
}
