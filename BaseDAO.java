package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {
    protected String filename;
    
    protected BaseDAO(String filename) {
        this.filename = filename;
        createFileIfNotExists();
    }
    
    protected void createFileIfNotExists() {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    
    protected void writeAllLines(List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected abstract T parseLine(String line);
    protected abstract String formatLine(T item);
} 