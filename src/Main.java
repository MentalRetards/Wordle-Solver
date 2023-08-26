import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static Instant startTime;
    public static List<String> all = new ArrayList<>();
    public static List<String> allUntouched = new ArrayList<>();
    public static String lower = "qwertyuiopasdfghjklzxcvbnm";
    public static String upper = "QWERTYUIOPASDFGHJKLZXCVBNM";
    public static Answer answer;
    public static List<String> unused = new ArrayList<>();
    public static List<SomewhereLetterPos> used = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        startTime = Instant.now();
        Util.print("Starting wordle solver!");
        Util.print("Enter correct letters as capitals, used letters as lowercase and non correct with a - before");
        Util.print("Reading possible words..");
        File words = new File("words.txt");
        all = FileUtil.readFromFile(words);
        try {
            if (all.size() < 1) {
                Util.print("Aborting program word file empty or corrupted");
                return;
            }
        } catch (NullPointerException exception) {
            Util.print("Aborting program null error");
            return;
        }
        Util.print("Successfully read " + all.size() + " words");
        allUntouched = all;
        answer = new Answer();
        while (true) {
            Util.print("Awaiting word input");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            String input = reader.readLine();
            Util.print("Parsing input");
            if (input.equalsIgnoreCase("reset")) {
                reset();
                Util.print("RESET WORDLE!");
                continue;
            }
            if (!parseInput(input)) continue;
            Util.print("Current Answer : " + answer.getCurrent());
            Util.print("Finding invalid answers...");
            List<String> toRemove = new ArrayList<>();
            for (String str : all) {
                int cIndex = 0;
                for (char cha : str.chars().mapToObj(c -> (char) c).toList()) {
                    if (!doAnswerCheck(cha, cIndex)) {
                        toRemove.add(str);
                        break;
                    }
                    for (SomewhereLetterPos letterPos : used) {
                        if (letterPos.getIndex() == cIndex && String.valueOf(cha).equals(letterPos.getString()) && !answer.getAtIndex(cIndex).equals(String.valueOf(cha))) {
                            toRemove.add(str);
                            break;
                        }
                    }
                    if (unused.contains(String.valueOf(cha))) {
                        toRemove.add(str);
                        break;
                    }
                    cIndex ++;
                }
                for (SomewhereLetterPos use : used) {
                    if (!str.contains(use.getString())) {
                        toRemove.add(str);
                        break;
                    }
                }
            }
            Util.print("Removing invalid answers..");
            for (String str : toRemove) {
                all.remove(str);
            }
            Util.print("Successfully removed " + toRemove.size() + " answers!");
            toRemove.clear();
            Util.print(all.size() + " possible words left");
            if (all.size() <= 20) {
                Util.print(all);
            }
            suggestGoodAnswer();
        }
    }
    public static void reset() {
        Util.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        used.clear();
        unused.clear();
        all = allUntouched;
        answer = new Answer();

    }
    public static boolean usedContainsCharacter(char c) {
        for (SomewhereLetterPos str : used) {
            if (str.getString().contains(String.valueOf(c))) return true;
        }
        return false;
    }
    public static void goodAnswerHighMode() {
        int attemptFind = 5;
        int score = 0;
        List<String> goods = new ArrayList<>();
        while (true) {
            for (String str : all) {
                for (char cha : str.toCharArray()) {
                    if (unused.contains(String.valueOf(cha))) continue;
                    if (usedContainsCharacter(cha)) continue;
                    score++;
                }
                if (score >= attemptFind && !goods.contains(str)) {
                    goods.add(str);
                }
            }
            if (goods.size() > 1) {
                Collections.shuffle(goods);
                int maxCount = 0;
                for (String str : goods) {
                    maxCount ++;
                    Util.print("Found good answer : " + str);
                    if (maxCount > 20) return;
                }
                return;
            }
            if (attemptFind == 1) return;
            attemptFind--;
        }
    }
    public static void goodAnswerLowMode() {

    }
    public static void suggestGoodAnswer() {
        if (all.size() < 30) {
            goodAnswerLowMode();
            goodAnswerHighMode();
            return;
        }
        goodAnswerHighMode();
    }
    public static boolean doAnswerCheck(char cha, int index) {
        String ans = answer.getAtIndex(index);
        if (ans.equals("-")) return true;
        if (ans.equals(String.valueOf(cha))) return true;
        return false;
    }
    public static boolean parseInput(String input) {
        if (input.replaceAll("-", "").length() != 5) {
            Util.print("Incorrect number of characters");
            return false;
        }
        int index = -1;
        int charIndex = -1;
        List<Character> chas = input.chars().mapToObj(c -> (char) c).toList();
        for (char cha : chas) {
            if (!(lower + "-").contains(String.valueOf(cha).toLowerCase())) {
                Util.print("invalid character was given");
                return false;
            }
            index ++;
            if (String.valueOf(cha).equals("-")) continue;
            charIndex ++;
            if (index > 0) if (String.valueOf(chas.get(index - 1)).equals("-") && !usedContainsCharacter(cha)) {
                unused.add(String.valueOf(cha));
                continue;
            }
            if (lower.contains(String.valueOf(cha))) used.add(new SomewhereLetterPos(String.valueOf(cha), charIndex));
            if (upper.contains(String.valueOf(cha))) {
                used.add(new SomewhereLetterPos(String.valueOf(cha).toLowerCase(), charIndex));
                answer.setChar(charIndex, cha);
            }
        }
        return true;
    }
}