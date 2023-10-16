import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WordleHandler {
    private String word;
    private List<Character> wordChar = new ArrayList<>();
    private List<String> allWords = new ArrayList<>();
    public WordleHandler(List<String> words) {
        this.word = words.get(new Random().nextInt(words.size() + 1));
        Util.print("Answer is : " + word);
        for (char c : this.word.toCharArray()) {
            wordChar.add(c);
        }
        allWords.addAll(words);
    }
    public String guess(String guess) {
        Util.print("Guessing " + guess);
        guess = guess.toLowerCase();
        if (!allWords.contains(guess)) {
            //Util.print(allWords);
            Util.print("Invalid guess!");
            return null;
        }
        if (guess.equals(this.word)) {
            Util.print("Word was found!");
            Util.print("Word was : " + word);
            return null;
        }
        StringBuilder returnValue = new StringBuilder();
        int index = -1;
        for (char cha : guess.toCharArray()) {
            index ++;
            if (String.valueOf(cha).equals(String.valueOf(wordChar.get(index)))) {
                returnValue.append(String.valueOf(cha).toUpperCase());
                continue;
            }
            if (word.contains(String.valueOf(cha))) {
                returnValue.append(String.valueOf(cha).toLowerCase());
                continue;
            }
            returnValue.append("-").append(cha);
        }
        return returnValue.toString();
    }
}
