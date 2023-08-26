import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {

    public static File createFile(String filename, File directory) {
        File file = new File(directory.getAbsolutePath() + "\\" + filename);
        if (file.exists()) if (!file.delete()) return null;
        try {
            file.createNewFile();
        } catch (IOException exception) {
            return null;
        }
        return file;
    }
    public static void writeToFile(String str, File f1) {
        try {
            FileWriter fw = new FileWriter(f1.getAbsolutePath());
            fw.write(str);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> readFromFile(File file) {
        List<String> contents = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                contents.add(scanner.nextLine());
            }
            scanner.close();
            return contents;
        } catch (FileNotFoundException exception) {
            return null;
        }
    }
}
