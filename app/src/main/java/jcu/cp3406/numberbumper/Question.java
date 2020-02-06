package jcu.cp3406.numberbumper;

import java.util.List;
import java.util.Stack;

class Question extends GameHeader {

    private int timeLapse;

    int getTimeLapse() {
        return timeLapse;
    }

    void setTimeLapse(int value) {
        timeLapse = value;
    }

    private int answer;

    int getAnswer() {
        return answer;
    }

    private Stack<Integer> choices = new Stack<>();

    int getChoice(int index) {
        return choices.get(index);
    }

    private Stack<Integer> entries = new Stack<>();

    int countEntries() {
        return entries.size();
    }

    int getEntry(int index) {
        return entries.get(index);
    }

    void addEntry(int entry) {
        entries.add(entry);
    }

    static final int MIN_ENTRIES_RATIO = 2;

    private static final int TIME_PER_CHOICE = 2;

    int getTimeLimit() {
        return TIME_PER_CHOICE * size * size;
    }

    Question(int level, int difficulty) {
        super(level, difficulty);
        // Populate unique random numbers:
        for (int i = 0; i < size * size; ++i) {
            int number;
            do {
                number = makeRandomNumber(-range, +range, choices);
            } while (number == 0); // Ensure the number is not zero!
            choices.add(number);
        }
        int counter = 0;
        do {
            // At least half the choices will be used to generate an answer:
            Stack<Integer> sampleIndices = new Stack<>();
            for (int i = 0; i < choices.size() / MIN_ENTRIES_RATIO; ++i) {
                sampleIndices.add(makeRandomNumber(0, choices.size() - 1, sampleIndices));
            }
            // Generate the sample of choices that will equate to an answer:
            Stack<Integer> sampleOperands = new Stack<>();
            for (int i : sampleIndices) {
                sampleOperands.add(choices.get(i));
            }
            // Generate an answer based on the samples:
            answer = calculateAnswer(level, sampleOperands);
        } while (choices.contains(answer) && ++counter <= MAX_RANDOM_ATTEMPTS); // Ensure the answer is not a choice!
    }

    static final int MAX_RANDOM_ATTEMPTS = 1000;

    private static int makeRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }

    private static int makeRandomNumber(int min, int max, List<Integer> list) {
        int number, counter = 0;
        do {
            number = makeRandomNumber(min, max);
        } while (list.contains(number) && ++counter <= MAX_RANDOM_ATTEMPTS);
        return number;
    }

    private static int calculateAnswer(int level, Stack<Integer> operands) {
        if (operands.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        int operandId = 0, result = operands.get(operandId);
        while (++operandId < operands.size()) {
            int operand = operands.get(operandId);
            switch (level) {
                case LEVEL_ADDITION:
                    result += operand;
                    break;
                case LEVEL_SUBTRACTION:
                    result -= operand;
                    break;
                case LEVEL_MULTIPLICATION:
                    result *= operand;
                    break;
                case LEVEL_DIVISION:
                    result /= operand;
                    break;
                case LEVEL_EXPONENTIATION:
                    result = (int) Math.pow(result, operand);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return result;
    }

    enum Status {
        PENDING,
        CORRECT,
        INCORRECT,
    }

    Status status() {
        if (entries.isEmpty()) {
            return Status.PENDING;
        }
        if (answer == calculateAnswer(level, entries)) {
            return Status.CORRECT;
        }
        if (entries.size() >= choices.size()) {
            return Status.INCORRECT;
        }
        return Status.PENDING;
    }
}
