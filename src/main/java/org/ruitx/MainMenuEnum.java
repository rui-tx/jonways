package org.ruitx;

public enum MainMenuEnum {
    LOAD(1, "1", "Load world from file"),
    RNDSTABLE(2, "2", "Generate random world that is stable"),
    RANDOM(3, "3", "Generate random world"),
    PRINT(8, "8", "Print to console the current selected world"),
    DRAW(9, "9", "Draw current selected world"),
    EXIT(0, "0", "Exit");

    private int id;
    private String option;
    private String desc;

    MainMenuEnum(int id, String option, String desc) {
        this.id = id;
        this.option = option;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getOption() {
        return option;
    }

    public String getDesc() {
        return desc;
    }
}
