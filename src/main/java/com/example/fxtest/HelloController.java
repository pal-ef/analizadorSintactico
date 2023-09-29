package com.example.fxtest;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class HelloController {
    @FXML
    private TextArea textArea;

    @FXML
    private Button revertButton;

    @FXML
    private Button analizeButton;

    public static String result = "";


    @FXML
    protected void onRevertClick() {
        // Emtpy global result
        result = "";

        // Empty textArea
        textArea.setText("");

        // Set button disabled and analyze enabled
        revertButton.setDisable(true);
        analizeButton.setDisable(false);
        textArea.setEditable(true);
    }

    @FXML
    protected void onAnalizeClick() {
        // Populate language dictionary
        populateDictionary();

        // Get content for lexer
        String content = textArea.getText();

        // Empty Input
        textArea.setText("");

        // Real-time built string
        StringBuilder currentString = new StringBuilder();

        // Auxiliary variables
        int quoteCount = 0;
        int parenthesisCount = 0;

        // Read character by character
        for(int i = 0; i < content.length(); i++) {
            // Get current char
            char current = content.charAt(i);

            // Check for quotes
            if(current == '"') quoteCount++;

            // if quoteCount is no a pair skip
            if(quoteCount % 2 != 0) {
                // Add this char to the currentString
                currentString.append(current);
                // Skip
                continue;
            }

            if(current == '(') {
                result += "✅ Paréntesis Inicial: ( \n";
                continue;
            };
            if(current == ')') {
                result += "✅ Paréntesis Final: ) \n";
                continue;
            };

            // Watch for spaces while not inside parenthesis
            if(Character.isWhitespace(current) || current == ';') {

                // If current string is empty skip process
                if(currentString.isEmpty()) {
                    continue;
                }

                // Check for symbol
                isSymbol(currentString.toString());
                textArea.setText(result);

                // Empty the currentString
                currentString = new StringBuilder();

                // if semicolon
                if(current == ';') {
                    textArea.setText(textArea.getText() + "\uD83D\uDE48 Punto y coma");
                }

                // skip adding this space
                continue;
            }

            // Add this char to the currentString
            currentString.append(current);
        }

        // Lastly, there can be another symbol at the end.
        // Verify that the currentString is not empty
        // if not then verify if it's a symbol
        if(!currentString.toString().isBlank()) {
            isSymbol(currentString.toString());
        }

        // Disable analyzeButton and enable revert
        analizeButton.setDisable(true);
        revertButton.setDisable(false);

        textArea.setEditable(false);
    }

    public static HashMap<String, String> dictionary = new HashMap<String, String>();

    private void populateDictionary() {
        // Read file with language rules
        try {
            // this might become a bug !!
            File object = new File("/Users/egpalaci/Documents/java/fxtest/src/main/resources/com/example/fxtest/rules.miku");
            Scanner scan = new Scanner(object);

            // Read every line and add it to local dictionary
            while(scan.hasNextLine()) {
                String data = scan.nextLine();
                if (data.isBlank()) continue;
                String[] rule = data.split(":");
                dictionary.put(rule[0], rule[1]);
            }

            System.out.println("\uD83D\uDFE2 Language rules successfully loaded!");
        } catch (FileNotFoundException e) {
            System.out.println("Language rules not found.");
        }
    }

    private static boolean validateIdentifier(String s) {
        String regexPattern = "^[a-zA-Z_$][a-zA-Z_$0-9]*$";
        return Pattern.compile(regexPattern).matcher(s).matches();
    }

    private static void isSymbol(String s) {
        // Verify that current symbol is valid inside language dictionary
        if(dictionary.containsKey(s)) {
            //System.out.println("✅ Recognized: " + dictionary.get(s) + "(" + s + ")");
            //result += "✅ Recognized: " + dictionary.get(s) + "(" + s + ")\n";
            result += "✅ " + dictionary.get(s) + ": " + s + '\n';
        } else {
            // Verify if symbol is real number
            try {
                float num = Float.parseFloat(s);
                result += "✅ Número flotante: " + num + '\n';
                return;
            } catch (NumberFormatException e) {

                // Verify if symbol is number
                try {
                    int num = Integer.parseInt(s);
                    //System.out.println("✅ Recognized: Integer" +  "(" + num + ")");
                    //result += "✅ Recognized: Integer" +  "(" + num + ")\n";
                    result += "✅ Número entero: " + num + '\n';
                } catch (NumberFormatException error) {
                    // verify if symbol is string
                    if (s.charAt(0) == '"') {
                        //System.out.println("✅ Recognized: content(" + s + ")");
                        result += "✅ Cadena de texto: " + s + "\n";
                        return;
                    }

                    // verify if valid identifier, probably using regex
                    if (validateIdentifier(s)) {
                        //System.out.println("✅ Recognized: identifier(" + s + ")");
                        result += "✅ Identificador: " + s + "\n";
                    } else {
                        //System.out.println("❌ Unrecognized: " + s);
                        result += "❌ Desconocido: " + s + "\n";
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}