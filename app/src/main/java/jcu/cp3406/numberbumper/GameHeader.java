package jcu.cp3406.numberbumper;

import android.content.Context;

abstract class GameHeader {

    static final int LEVEL_ADDITION = 1;
    static final int LEVEL_SUBTRACTION = 2;
    static final int LEVEL_MULTIPLICATION = 3;
    static final int LEVEL_DIVISION = 4;
    static final int LEVEL_EXPONENTIATION = 5;

    int level;

    static String getLevelSymbol(int level) {
        switch (level) {
            case LEVEL_ADDITION:
                return "+";
            case LEVEL_SUBTRACTION:
                return "-";
            case LEVEL_MULTIPLICATION:
                return "×";
            case LEVEL_DIVISION:
                return "÷";
            case LEVEL_EXPONENTIATION:
                return "•";
            default:
                return "?";
        }
    }

    static String getLevelWord(int level, Context context) {
        int stringId;
        switch (level) {
            case LEVEL_ADDITION:
                stringId = R.string.level_addition;
                break;
            case LEVEL_SUBTRACTION:
                stringId = R.string.level_subtraction;
                break;
            case LEVEL_MULTIPLICATION:
                stringId = R.string.level_multiplication;
                break;
            case LEVEL_DIVISION:
                stringId = R.string.level_division;
                break;
            case LEVEL_EXPONENTIATION:
                stringId = R.string.level_exponentiation;
                break;
            default:
                stringId = R.string.unknown;
        }
        return context.getString(stringId);
    }

    static final int DIFFICULTY_EASIEST = 1;
    static final int DIFFICULTY_NORMAL = 2;
    static final int DIFFICULTY_HARDEST = 3;

    static String getDifficultyWord(int difficulty, Context context) {
        int stringId;
        switch (difficulty) {
            case DIFFICULTY_EASIEST:
                stringId = R.string.difficulty_easiest;
                break;
            case DIFFICULTY_NORMAL:
                stringId = R.string.difficulty_normal;
                break;
            case DIFFICULTY_HARDEST:
                stringId = R.string.difficulty_hardest;
                break;
            default:
                stringId = R.string.unknown;
        }
        return context.getString(stringId);
    }

    int difficulty;

    int size;

    int getSize() {
        return size;
    }

    int range;

    GameHeader(int level, int difficulty) {
        this.level = level;
        this.difficulty = difficulty;
        switch (difficulty) {
            case DIFFICULTY_EASIEST:
                size = 2;
                range = 9;
                break;
            case DIFFICULTY_NORMAL:
                size = 3;
                range = 12;
                break;
            case DIFFICULTY_HARDEST:
                size = 4;
                range = 16;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
