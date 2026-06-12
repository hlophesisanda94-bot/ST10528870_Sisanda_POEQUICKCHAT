/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/** 
 * @author User
 */

package com.mycompany.poequickchat;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class POEQuickChat {
    // Part 1: Static variables
    static String storedUsername = "";
    static String storedPassword = "";
    static boolean isLoggedIn = false;

    // Part 2: Message tracking
    static int numMessagesSent = 0;
    static int messageLimit = 0;

    // Part 3: Arrays 
    static ArrayList<String> sentMessages = new ArrayList<>();
    static ArrayList<String> disregardedMessages = new ArrayList<>();
    static ArrayList<JSONObject> storedMessages = new ArrayList<>();
    static ArrayList<String> messageIDs = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();
    static ArrayList<String> recipients = new ArrayList<>();

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to QuickChat");
        loadStoredMessagesFromJSON(); // Load VC test data Messages 2 + 5

        while (true) {
            if (!isLoggedIn) {
                loginMenu();
            } else {
                mainMenu();
            }
        }
    }

    // ========== PART 1: REGISTRATION + LOGIN ==========
    public static boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public static boolean checkPasswordComplexity(String password) {
        boolean hasLength = password.length() >= 8;
        boolean hasCapital =!password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        return hasLength && hasCapital && hasNumber && hasSpecial;
    }

    public static String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number and a special character.";
        }
        storedUsername = username;
        storedPassword = password;
        return "Username and password successfully captured. User registered!";
    }

    public static void loginMenu() {
        System.out.println("\n=== REGISTRATION/LOGIN ===");
        System.out.println("1. Register\n2. Login\n3. Quit");
        System.out.print("Choice: ");
        String choice = input.nextLine();

        switch (choice) {
            case "1":
                System.out.print("Enter username: ");
                String username = input.nextLine();
                System.out.print("Enter password: ");
                String password = input.nextLine();
                System.out.println(registerUser(username, password));
                break;
            case "2":
                System.out.print("Enter username: ");
                String loginUser = input.nextLine();
                System.out.print("Enter password: ");
                String loginPass = input.nextLine();
                if (storedUsername.equals(loginUser) && storedPassword.equals(loginPass)) {
                    isLoggedIn = true;
                    System.out.println("Welcome " + loginUser + " it is great to see you again.");
                } else {
                    System.out.println("Username or password incorrect, please try again");
                }
                break;
            case "3":
                System.exit(0);
        }
    }

    // ========== PART 2 + 3: MAIN MENU ==========
    public static void mainMenu() {
        if (messageLimit == 0) {
            System.out.print("How many messages would you like to send? ");
            messageLimit = Integer.parseInt(input.nextLine());
        }

        System.out.println("\n=== QUICKCHAT MAIN MENU ===");
        System.out.println("1. Send Message");
        System.out.println("2. Show Recently Sent Messages");
        System.out.println("3. Quit");
        System.out.println("4. Stored Messages");
        System.out.print("Choice: ");
        String choice = input.nextLine();

        switch (choice) {
            case "1": createMessageFlow(); break;
            case "2": showSentMessages(); break;
            case "3":
                System.out.println("Total messages sent: " + Message.returnTotalMessages());
                System.out.println("Goodbye!");
                System.exit(0);
            case "4": storedMessagesMenu(); break;
            default: System.out.println("Invalid choice");
        }
    }

    // PART 2 LOGIC
    public static void createMessageFlow() {
        if (numMessagesSent >= messageLimit) {
            System.out.println("Message limit reached.");
            return;
        }

        System.out.print("Enter recipient cell number: ");
        String recipient = input.nextLine();

        System.out.print("Enter your message: ");
        String messageText = input.nextLine();

        if (messageText.length() > 250) {
            System.out.println("Please enter a message of less than 250 characters.");
            return;
        }

        Message msg = new Message(recipient, messageText);

        String cellResult = msg.checkRecipientCell();
        if (!cellResult.equals("Cell phone number successfully captured.")) {
            System.out.println(cellResult);
            return;
        }

        System.out.println("\nChoose one of the following options:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Enter option: ");

        int msgChoice = input.nextInt();
        input.nextLine();

        switch (msgChoice) {
            case 1: // Send
                sentMessages.add(msg.getMessage());
                messageIDs.add(msg.getMessageID());
                messageHashes.add(msg.getMessageHash());
                recipients.add(msg.getRecipient());
                numMessagesSent++;
                System.out.println("Message sent successfully!");
                System.out.println(msg.printMessages());
                break;
            case 2: // Disregard
                disregardedMessages.add(msg.getMessage());
                System.out.println("Message disregarded.");
                break;
            case 3: // Store
                msg.storeMessage();
                System.out.println("Message stored successfully. ID: " + msg.getMessageID());
                break;
        }
    }

    public static void showSentMessages() {
        System.out.println("--- SENT MESSAGES ---");
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages");
            return;
        }
        for (int i = 0; i < sentMessages.size(); i++) {
            System.out.println("ID: " + messageIDs.get(i) + " | To: " + recipients.get(i) + " | Msg: " + sentMessages.get(i));
        }
    }

    // ========== PART 3: STORED MESSAGES MENU ==========
    public static void storedMessagesMenu() {
        String choice;
        do {
            System.out.println("\n=== STORED MESSAGES MENU ===");
            System.out.println("a. Display sender and recipient of all stored messages");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message ID");
            System.out.println("d. Search all messages for a particular recipient");
            System.out.println("e. Delete a message using message hash");
            System.out.println("f. Display full report of all stored messages");
            System.out.println("g. Back to main menu");
            System.out.print("Choice: ");
            choice = input.nextLine();

            switch (choice) {
                case "a": 
                    for (JSONObject msg : storedMessages) {
                        System.out.println("ID: " + msg.get("id") + " | From: " + msg.get("sender") + " | To: " + msg.get("recipient"));
                    }
                    break;
                case "b": 
                    int maxLen = 0;
                    String longest = "";
                    String longestID = "";
                    for (JSONObject msg : storedMessages) {
                        String message = (String) msg.get("message");
                        if (message.length() > maxLen) {
                            maxLen = message.length();
                            longest = message;
                            longestID = (String) msg.get("id");
                        }
                    }
                    System.out.println("Longest Message ID: " + longestID);
                    System.out.println("Message: " + longest);
                    break;
                case "c": 
                    System.out.print("Enter Message ID: ");
                    String searchID = input.nextLine();
                    for (JSONObject msg : storedMessages) {
                        if (msg.get("id").equals(searchID)) {
                            System.out.println("Recipient: " + msg.get("recipient"));
                            System.out.println("Message: " + msg.get("message"));
                        }
                    }
                    break;
                case "d": // 
                    System.out.print("Enter recipient number: ");
                    String recipient = input.nextLine();
                    for (JSONObject msg : storedMessages) {
                        if (msg.get("recipient").equals(recipient)) {
                            System.out.println("ID: " + msg.get("id") + " | Msg: " + msg.get("message"));
                        }
                    }
                    break;
                case "e": // 
                    System.out.print("Enter message hash to delete: ");
                    String hash = input.nextLine();
                    storedMessages.removeIf(msg -> msg.get("hash").equals(hash));
                    saveToJSON();
                    System.out.println("Message deleted");
                    break;
                case "f": // 
                    System.out.println("====== STORED MESSAGES FULL REPORT ======");
                    for (JSONObject msg : storedMessages) {
                        System.out.println("----------------------------------------");
                        System.out.println("Message ID: " + msg.get("id"));
                        System.out.println("Hash: " + msg.get("hash"));
                        System.out.println("Sender: " + msg.get("sender"));
                        System.out.println("Recipient: " + msg.get("recipient"));
                        System.out.println("Message: " + msg.get("message"));
                    }
                    break;
            }
        } while (!choice.equals("g"));
    }

    // JSON File Handling
    public static void loadStoredMessagesFromJSON() {
        JSONParser parser = new JSONParser();
        try {
            JSONArray arr = (JSONArray) parser.parse(new FileReader("storedMessages.json"));
            storedMessages.clear();
            for (Object obj : arr) {
                storedMessages.add((JSONObject) obj);
            }
        } catch (Exception e) {
            
        }
    }

    public static void saveToJSON() {
        try (FileWriter file = new FileWriter("storedMessages.json")) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(storedMessages);
            file.write(jsonArray.toJSONString());
        } catch (Exception e) {
            System.out.println("Error saving JSON: " + e.getMessage());
        }
    }

    // ========== PART 2: MESSAGE CLASS ==========
    static class Message {
        private String messageID;
        private String recipient;
        private String message;
        private String messageHash;
        private int messageNum;
        private static ArrayList<Message> sentMessages = new ArrayList<>();
        private static int totalMessages = 0;

        // Constructor - VC requires MSG001 format
        public Message(String recipient, String message) {
            this.recipient = recipient;
            this.message = message;
            this.messageNum = ++totalMessages;
            this.messageID = "MSG" + String.format("%03d", this.messageNum);
            this.messageHash = createMessageHash();
            sentMessages.add(this);
        }

        public boolean checkMessageID() {
            return messageID.length() <= 10;
        }

        public String checkRecipientCell() {
            if (recipient.length() == 12 && recipient.startsWith("+") && recipient.substring(1).matches("\\d+")) {
                return "Cell phone number successfully captured.";
            }
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

       
        public String createMessageHash() {
            String[] words = message.trim().split("\\s+");
            String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            return String.format("%02d:%d:%s%s", messageNum, message.length(), firstWord, lastWord);
        }

        public void storeMessage() {
            JSONObject storedMsg = new JSONObject();
            storedMsg.put("id", this.messageID);
            storedMsg.put("sender", POEQuickChat.storedUsername);
            storedMsg.put("recipient", this.recipient);
            storedMsg.put("message", this.message);
            storedMsg.put("hash", this.messageHash);
            POEQuickChat.storedMessages.add(storedMsg);
            POEQuickChat.saveToJSON();
        }

        public String printMessages() {
            return "Message ID: " + messageID +
                   "\nMessage Hash: " + messageHash +
                   "\nRecipient: " + recipient +
                   "\nMessage: " + message;
        }

        public static int returnTotalMessages() {
            return totalMessages;
        }

        // Getters
        public String getMessageID() { return messageID; }
        public String getRecipient() { return recipient; }
        public String getMessage() { return message; }
        public String getMessageHash() { return messageHash; }
    }
}