import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class Main {
    public static long startTime;
    public static List<String> all = new ArrayList<>();
    public static List<String> allUntouched = new ArrayList<>();
    public static boolean autoPlay = false;
    public static String nextGuess;
    public static String defaultGuess = "crane";
    public static WordleHandler wordleHandler;
    public static String lower = "qwertyuiopasdfghjklzxcvbnm";
    public static String upper = "QWERTYUIOPASDFGHJKLZXCVBNM";
    public static List<CharacterScore> scores = new ArrayList<>();
    public static Answer answer;
    private static int MAX_WORDS_TO_SHOW = 20;
    private static int LOW_MODE_THRESHOLD = 2;
    private static int MAX_GOOD_ANSWER_SHOW = 1;
    public static List<String> unused = new ArrayList<>();
    public static List<SomewhereLetterPos> used = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        if (autoPlay) {
            LOW_MODE_THRESHOLD = 1;
        }
        startTime = System.nanoTime();
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
        allUntouched.addAll(all);
        if (autoPlay) {
            wordleHandler = new WordleHandler(all);
        }
        answer = new Answer();
        Util.print("Calculating letter scores..");
        calculateNumberScores();
        Util.print("Done!");
        if (autoPlay) nextGuess = tryGuess(defaultGuess);
        while (true) {
            String input = "";
            Util.print("Awaiting word input");
            if (!autoPlay) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in));
                input = reader.readLine();
            } else {
                input = getGuess();
            }
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
            if (all.size() <= MAX_WORDS_TO_SHOW) {
                Util.print(all);
            }
            if (autoPlay && all.size() == 1) {
                guess(all.get(0));
            }
            suggestGoodAnswer();
        }
    }
    public static String getGuess() {
        return nextGuess;
    }
    public static void calculateNumberScores() {
        scores.clear();
        for (char cha : lower.toCharArray()) {
            scores.add(new CharacterScore(cha));
        }
        for (String str : allUntouched) {
            for (char cha : str.toCharArray()) {
                getScoreByChar(cha).addScore();
            }
        }
    }
    public static CharacterScore getScoreByChar(String character) {
        for (CharacterScore score : scores) {
            if (score.getCharacter().equals(character)) return score;
        }
        return null;
    }
    public static CharacterScore getScoreByChar(char character) {
        return getScoreByChar(String.valueOf(character));
    }

        public static void reset() {
        Util.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        used.clear();
        unused.clear();
        all = allUntouched;
        answer = new Answer();

    }
    public static boolean usedContainsCharacter(char c, int index) {
        for (SomewhereLetterPos str : used) {
            if (str.getString().contains(String.valueOf(c)) && str.getIndex() != index) return true;
        }
        return false;
    }
    public static boolean usedGreenCharacters(String str) {
        int index = 0;
        for (char cha : str.toCharArray()) {
            if (usedGreenCharacters(cha, index)) return true;
            index ++;
        }
        return false;
    }
    public static boolean usedGreenCharacters(char c, int index) {
        return answer.getCurrent().contains(String.valueOf(c));
    }
    public static void goodAnswerHighMode() {
        int attemptFind = 10;
        int score = 0;
        List<String> goods = new ArrayList<>();
        List<String> currentChars = new ArrayList<>();
        while (true) {
            for (String str : allUntouched) {
                currentChars.clear();
                score = 0;
                int index = -1;
                for (char cha : str.toCharArray()) {
                    index ++;
                    if (currentChars.contains(String.valueOf(cha))) continue;
                    currentChars.add(String.valueOf(cha));
                    if (unused.contains(String.valueOf(cha))) continue;
                    //if (usedContainsCharacter(cha) && usedGreenCharacters(cha, index)) continue;
                    if (usedGreenCharacters(cha, index)) continue;
                    if (usedContainsCharacter(cha, index)) score ++;
                    score++;
                }
                if (score >= attemptFind && !goods.contains(str)) {
                    //Util.print("Added good " + str + " with score " + score + " and goal of " + attemptFind);
                    goods.add(str);
                }
            }
            if (goods.size() > 1) {
                String good = getBestCharacterScore(goods);
                Util.print("Found HIGH good answer : " + good);
                guess(good);
                return;
            }
            if (attemptFind == 1) return;
            attemptFind--;
        }
    }
    public static String tryGuess(String guess) {
        return wordleHandler.guess(guess);
    }
    public static void guess(String guess) {
        if (!autoPlay) return;
        nextGuess = wordleHandler.guess(guess);
    }
    public static int calculateCharScore(String str) {
        int score = 0;
        for (char cha : str.toCharArray()) {
            score += getScoreByChar(cha).getScore();
        }
        return score;
    }
    public static String getBestCharacterScore(List<String> goods) {
        int best = 0;
        String bestStr = "";
        for (String str : goods) {
            int score = calculateCharScore(str);
            if (score > best) {
                best = score;
                bestStr = str;
            }
        }
        return bestStr;
    }
    public static void goodAnswerLowMode() {
        int attemptFind = 5;
        int score;
        List<String> goods = new ArrayList<>();
        while (true) {
            for (String str : all) {
                score = 0;
                int index = -1;
                for (char cha : str.toCharArray()) {
                    index ++;
                    if (unused.contains(String.valueOf(cha))) continue;
                    if (usedContainsCharacter(cha, index)) continue;
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
                    Util.print("Found LOW good answer : " + str);
                    guess(str);
                    if (maxCount > 20) return;
                }
                return;
            }
            if (attemptFind == 1) return;
            attemptFind--;
        }
    }
    public static void suggestGoodAnswer() {
        if (all.size() <= LOW_MODE_THRESHOLD) {
            goodAnswerLowMode();
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
            if (index > 0) if (String.valueOf(chas.get(index - 1)).equals("-") && !usedContainsCharacter(cha, index)) {
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