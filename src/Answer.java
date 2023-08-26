public class Answer {
    String char1 = "-";
    String char2 = "-";
    String char3 = "-";
    String char4 = "-";
    String char5 = "-";
    public Answer() {

    }
    public void setChar(int index, char cha) {
        if (index == 0) setChar1(cha);
        if (index == 1) setChar2(cha);
        if (index == 2) setChar3(cha);
        if (index == 3) setChar4(cha);
        if (index == 4) setChar5(cha);
    }
    public void setChar1(char cha) {
        char1 = String.valueOf(cha).toLowerCase();
    }
    public void setChar2(char cha) {
        char2 = String.valueOf(cha).toLowerCase();
    }
    public void setChar3(char cha) {
        char3 = String.valueOf(cha).toLowerCase();
    }
    public void setChar4(char cha) {
        char4 = String.valueOf(cha).toLowerCase();
    }
    public void setChar5(char cha) {
        char5 = String.valueOf(cha).toLowerCase();
    }
    public String getCurrent() {
        return char1 + char2 + char3 + char4 + char5;
    }
    public String getAtIndex(int index) {
        if (index == 0) return char1;
        if (index == 1) return char2;
        if (index == 2) return char3;
        if (index == 3) return char4;
        if (index == 4) return char5;
        return "?";
    }
}
