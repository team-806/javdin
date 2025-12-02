package com.javdin.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects and manages compilation and runtime errors and informational messages.
 */
public class ErrorHandler {
    private final List<Message> messages;
    
    public ErrorHandler() {
        this.messages = new ArrayList<>();
    }
    
    public void addError(String message, int line, int column) {
        messages.add(new Error(message, line, column));
    }
    
    public void addInfo(String message, int line, int column) {
        messages.add(new Info(message, line, column));
    }
    
    public void addWarning(String message, int line, int column) {
        messages.add(new Warning(message, line, column));
    }
    
    public boolean hasErrors() {
        return messages.stream().anyMatch(m -> m instanceof Error);
    }
    
    public void printErrors() {
        for (Message message : messages) {
            if (message instanceof Error) {
                System.err.println(message);
            }
        }
    }
    
    public void printInfo() {
        for (Message message : messages) {
            if (message instanceof Info) {
                System.out.println(message);
            }
        }
    }
    
    public void printAll() {
        for (Message message : messages) {
            if (message instanceof Error) {
                System.err.println(message);
            } else {
                System.out.println(message);
            }
        }
    }
    
    public List<Error> getErrors() {
        List<Error> errorList = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof Error) {
                errorList.add((Error) message);
            }
        }
        return errorList;
    }

    /**
     * Returns all collected messages (errors, infos, warnings) in insertion order.
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public boolean hasInfo() {
        return messages.stream().anyMatch(m -> m instanceof Info);
    }
    
    public List<Info> getInfoMessages() {
        List<Info> infoList = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof Info) {
                infoList.add((Info) message);
            }
        }
        return infoList;
    }
    
    public void clear() {
        messages.clear();
    }
    
    public abstract static class Message {
        protected final String message;
        protected final int line;
        protected final int column;
        
        public Message(String message, int line, int column) {
            this.message = message;
            this.line = line;
            this.column = column;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        public abstract String toString();
    }
    
    public static class Error extends Message {
        public Error(String message, int line, int column) {
            super(message, line, column);
        }
        
        @Override
        public String toString() {
            if (line > 0 && column > 0) {
                return String.format("Error at line %d, column %d: %s", line, column, message);
            } else {
                return "Error: " + message;
            }
        }
    }
    
    public static class Info extends Message {
        public Info(String message, int line, int column) {
            super(message, line, column);
        }
        
        @Override
        public String toString() {
            if (line > 0 && column > 0) {
                return String.format("Info: at line %d, column %d: %s", line, column, message);
            } else {
                return "Info: " + message;
            }
        }
    }
    
    public static class Warning extends Message {
        public Warning(String message, int line, int column) {
            super(message, line, column);
        }
        
        @Override
        public String toString() {
            if (line > 0 && column > 0) {
                return String.format("Warning at line %d, column %d: %s", line, column, message);
            } else {
                return "Warning: " + message;
            }
        }
    }
}
