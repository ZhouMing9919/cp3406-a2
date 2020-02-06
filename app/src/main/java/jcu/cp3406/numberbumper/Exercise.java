package jcu.cp3406.numberbumper;

import java.util.Stack;

class Exercise extends GameHeader {

    private int questionId = -1;

    int getQuestionId() {
        return questionId;
    }

    boolean isQuestionIdValid() {
        return questionId >= 0 && questionId < questions.size();
    }

    private Stack<Question> questions = new Stack<>();

    boolean isFinished() {
        return questionId >= questions.size();
    }

    Question getCurrentQuestion() {
        if (isQuestionIdValid()) {
            return questions.get(questionId);
        }
        return null;
    }

    Question getNextQuestion() {
        questionId++;
        return getCurrentQuestion();
    }

    Question getRetryQuestion() {
        Question question = new Question(level, difficulty);
        if (isQuestionIdValid()) {
            questions.set(questionId, question);
        }
        return question;
    }

    int countAllQuestions() {
        return questions.size();
    }

    int countCorrectQuestions() {
        int counter = 0;
        for (Question question : questions) {
            if (question.status() == Question.Status.CORRECT) {
                counter += 1;
            }
        }
        return counter;
    }

    static final int MAX_TRIES = 3;

    private int tries = MAX_TRIES;

    int getTries() {
        return tries;
    }

    boolean decrementTries() {
        return --tries >= 0;
    }

    static final int MAX_QUESTIONS = 10;

    Exercise(int level, int difficulty) {
        super(level, difficulty);
        for (int i = 0; i < MAX_QUESTIONS; ++i) {
            questions.add(new Question(level, difficulty));
        }
    }
}
