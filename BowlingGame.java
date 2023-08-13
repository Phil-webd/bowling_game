import java.util.*;

class BowlingGame {

    private List<Frame> frames = new ArrayList<>();
    private Frame currentFrame = new Frame();
    private int bonus = 0;
    private boolean gameOver = false;
    private int rollCount = 0;

    public void roll(int pins) throws IllegalStateException {

        rollCount++;

        System.out.println("Roll count: " + rollCount);

        if (pins < 0) {
            throw new IllegalStateException("Negative roll is invalid.");
        }

        if (pins > 10) {
            throw new IllegalStateException("Invalid number of pins.");
        }

        if (gameOver) {
            throw new IllegalStateException("Game is finished.");
        }

        if (frames.size() < 9) {

            currentFrame.pins(pins);

            if (currentFrame.isDone()) {

                frames.add(currentFrame);

                System.out.println("Frame " + frames.size() + ": Score is " + currentFrame.getScore());

                currentFrame = new Frame();
            }

        } else if (frames.size() == 9) {

            currentFrame.pins(pins);

            if (currentFrame.isDone()) {

                frames.add(currentFrame);

                System.out.println("Frame " + frames.size() + ": Score is " + currentFrame.getScore());

                if (!currentFrame.isSpare() && !currentFrame.isStrike()) {
                    gameOver = true;
                }
            }

        } else if (frames.size() == 10) {

            Frame lastFrame = frames.get(9);

            if (lastFrame.isSpare()) {
                System.out.println("Last Frame was a spare.");
                lastFrame.add(pins);
                gameOver = true;
            }

            else if (lastFrame.isStrike()) {

                if (bonus == 0) {
                    bonus += pins;
                }

                else {
                    if (bonus != 10 && bonus + pins > 10) {
                        throw new IllegalStateException("Invalid number of pins.");
                    }

                    currentFrame.add(bonus + pins);
                    gameOver = true;
                }
            }
        }
    }

    public int numFrames() {
        return frames.size();
    }

    private void spareBonus() {

        Frame thisFrame;

        for (int i = 0; i < frames.size() - 2; i++) {

            thisFrame = frames.get(i);

            if (thisFrame.isSpare()) {
                thisFrame.add(frames.get(i + 1).getFirstRoll());
            }
        }
    }

    private void strikeBonus() {

        Frame thisFrame;
        Frame nextFrame;
        Frame thirdFrame;

        for (int i = 0; i < frames.size() - 2; i++) {

            thisFrame = frames.get(i);

            nextFrame = frames.get(i + 1);

            thirdFrame = frames.get(i + 2);

            if (thisFrame.isStrike()) {
                if (nextFrame.isStrike() && thirdFrame.isStrike()) {
                    thisFrame.add(nextFrame.getScore() + thirdFrame.getScore());
                }

                else if (nextFrame.isStrike()) {
                    thisFrame.add(nextFrame.getScore() + thirdFrame.getFirstRoll());
                }

                else {
                    thisFrame.add(nextFrame.getScore());
                }
            }
        }
    }

     public int score() {
        if (!gameOver) {
            throw new IllegalStateException("Score can only be calculated after the game is finished.");
        }

        strikeBonus();
        spareBonus();

        return frames.stream()
                .mapToInt(Frame::getScore)
                .sum();
    }

    private static class Frame {

        private boolean done = false;
        private int rolls = 0;
        private int firstRoll = 0;
        private int secondRoll = 0;
        private int bonus = 0;

        public void pins(int roll) throws IllegalStateException {

            if (done) {
                return;
            }

            if (firstRoll + secondRoll + roll > 10) {
                throw new IllegalStateException("Invalid number of pins.");
            }

            if (rolls == 0) {
                if (roll == 10) {
                    done = true;
                }

                firstRoll = roll;
                rolls++;
            } else {
                secondRoll = roll;
                rolls++;
                done = true;
            }
        }

        public int getFirstRoll() {
            return firstRoll;
        }

        public void add(int bonus) {

            if (isSpare() || isStrike()) {
                this.bonus += bonus;
            }
        }

        public int getScore() {
            return firstRoll + secondRoll + bonus;
        }

        public boolean isDone() {
            return done;
        }

        public boolean isSpare() {
            return firstRoll + secondRoll == 10 && rolls == 2;
        }

        public boolean isStrike() {
            return firstRoll == 10;
        }
    }

    public static void main(String[] args) {

        BowlingGame bowlingGame = new BowlingGame();

        //CLI input
        //input numbers separated by a space (take the commented out numbers below as reference)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Rolls for this match: ");

        String input = scanner.nextLine();
        List<Integer> rollsInput = new ArrayList<Integer>();

        String[] str = input.trim().split("\\s+");
        for (int i = 0; i < str.length; i++) {
            rollsInput.add(Integer.parseInt(str[i]));
        }
        scanner.close();
        Integer[] rolls = rollsInput.toArray(Integer[]::new);

        //fixed values
        //int[] rolls = { 10, 10, 10, 5, 3, 4, 2, 6, 4, 10, 8, 2, 7, 3, 6, 1 };

        for (int pins : rolls) {
            bowlingGame.roll(pins);
        }

        System.out.println(bowlingGame.numFrames());

        System.out.println("Final Score: " + bowlingGame.score());
    }
}