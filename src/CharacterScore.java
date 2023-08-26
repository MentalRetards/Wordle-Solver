public class CharacterScore {
    String character;
    int score;
    public CharacterScore(char character) {
        this.character = String.valueOf(character);
    }
    public void addScore() {
        this.score ++;
    }
    public int getScore() {
        return this.score;
    }
    public String getCharacter() {
        return this.character;
    }
}
