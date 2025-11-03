package lotto;

import camp.nextstep.edu.missionutils.*;

import java.util.Collections;
import java.util.List;

public class LottoMachine {
    private final Integer lottoPrice = Constants.LOTTO_PRICE.getValue();
    private final Integer lottoMinNum = Constants.MIN_LOTTO_NUM.getValue();
    private final Integer lottoMaxNum = Constants.MAX_LOTTO_NUM.getValue();
    private final Integer lottoCount = Constants.LOTTO_RANDOM_BALL_COUNT.getValue() + Constants.LOTTO_BONUS_BALL_COUNT.getValue();
    private final Integer lottoBallCount = Constants.LOTTO_RANDOM_BALL_COUNT.getValue();

    private Integer[] myLotto;
    private Integer[] winnerBall;
    private Integer winnerBonusBall;

    public void start() {
        Integer lottoCount = buyLotto();

        getLottoNumbers(lottoCount);

        enterWinningNumbers();

        printWinningStatistics(lottoCount);
    }

    /**
     * 1. 결제 및 로또 개수 반환
     */
    private Integer buyLotto() {
        // 1. 구입 금액 입력
        System.out.println("구입금액을 입력해 주세요.");
        String paidPrice = Console.readLine();

        System.out.println(); // 줄바꿈용

        return calcLottoCount(Integer.valueOf(paidPrice));
    }

    /**
     * 2. 로또 번호 랜덤 발행
     * @param count 로또 개수
     */
    private void getLottoNumbers(Integer count) {
        myLotto = new Integer[count * lottoCount]; // 각 로또당 6개의 번호
        int index = 0;

        System.out.println(count + "개를 구매했습니다.");

        for (int i = 0; i < count; i++) {
            index = generateAndPrintOneLotto(index, i, count);
        }
        System.out.println(); // 마지막 줄바꿈
    }

    private int generateAndPrintOneLotto(int index, int currentIndex, int totalCount) {
        // 랜덤 번호 생성 (1-45 중 6개)
        List<Integer> numbers = Randoms.pickUniqueNumbersInRange(lottoMinNum, lottoMaxNum, lottoCount);
        Collections.sort(numbers);

        // myLotto 배열에 저장
        for (Integer number : numbers) {
            myLotto[index++] = number;
        }

        // 발행된 로또 번호 출력
        printLottoNumbers(numbers, currentIndex, totalCount);
        return index;
    }

    private void printLottoNumbers(List<Integer> numbers, int currentIndex, int totalCount) {
        System.out.print(numbers);
        if (currentIndex < totalCount - 1) {
            System.out.println();
        }
    }

    /**
     * 3. 우승자 번호 입력
     */
    private void enterWinningNumbers() {
        System.out.println("당첨 번호를 입력해 주세요.");
        String winningBallNumbers = Console.readLine();
        Integer[] winningNumbers = validateWinningNumbers(winningBallNumbers);

        System.out.println("\n보너스 번호를 입력해 주세요.");
        String winningBonusBallNumbers = Console.readLine();
        validateBonusNumber(winningBonusBallNumbers, winningNumbers);

        winnerBall = winningNumbers; // 검증을 마치면 전역변수에 반영
        winnerBonusBall = Integer.valueOf(winningBonusBallNumbers);
    }

    /**
     * 4. 당첨 통계 출력
     */
    private void printWinningStatistics(Integer lottoCount) {
        int[] rankCount = calculateRankCount();
        printStatistics(rankCount);
        printProfitRate(rankCount, lottoCount);
    }

    private int[] calculateRankCount() {
        int[] rankCount = new int[6]; // 1등~5등 (인덱스 1~5 사용)
        int totalBoughtCount = myLotto.length / this.lottoCount;

        for (int i = 0; i < totalBoughtCount; i++) {
            Integer[] oneLotto = extractOneLotto(i);
            int rank = checkWinningRank(oneLotto);
            if (rank > 0) {
                rankCount[rank]++;
            }
        }
        return rankCount;
    }

    private Integer[] extractOneLotto(int lottoIndex) {
        Integer[] oneLotto = new Integer[lottoBallCount];
        for (int j = 0; j < lottoBallCount; j++) {
            oneLotto[j] = myLotto[lottoIndex * this.lottoCount + j];
        }
        return oneLotto;
    }

    private void printStatistics(int[] rankCount) {
        System.out.println("\n당첨 통계");
        System.out.println("---");
        System.out.println("3개 일치 (5,000원) - " + rankCount[5] + "개");
        System.out.println("4개 일치 (50,000원) - " + rankCount[4] + "개");
        System.out.println("5개 일치 (1,500,000원) - " + rankCount[3] + "개");
        System.out.println("5개 일치, 보너스 볼 일치 (30,000,000원) - " + rankCount[2] + "개");
        System.out.println("6개 일치 (2,000,000,000원) - " + rankCount[1] + "개");
    }

    private void printProfitRate(int[] rankCount, Integer lottoCount) {
        long totalPrize = (long) rankCount[5] * 5000
                        + (long) rankCount[4] * 50000
                        + (long) rankCount[3] * 1500000
                        + (long) rankCount[2] * 30000000
                        + (long) rankCount[1] * 2000000000L;

        int totalSpent = lottoCount * lottoPrice;
        double profitRate = (double) totalPrize / totalSpent * 100;

        System.out.printf("총 수익률은 %.1f%%입니다.\n", profitRate);
    }

    private int checkWinningRank(Integer[] oneLotto) {
        int matchCount = countMatchingNumbers(oneLotto);
        boolean bonusMatch = hasBonusMatch(oneLotto);

        return determineRank(matchCount, bonusMatch);
    }

    private int countMatchingNumbers(Integer[] oneLotto) {
        int matchCount = 0;
        for (Integer myNumber : oneLotto) {
            if (isWinningNumber(myNumber)) {
                matchCount++;
            }
        }
        return matchCount;
    }

    private boolean isWinningNumber(Integer number) {
        for (Integer winNumber : winnerBall) {
            if (number.equals(winNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBonusMatch(Integer[] oneLotto) {
        for (Integer myNumber : oneLotto) {
            if (myNumber.equals(winnerBonusBall)) {
                return true;
            }
        }
        return false;
    }

    private int determineRank(int matchCount, boolean bonusMatch) {
        if (matchCount == 6) {
            return 1; // 1등
        }
        if (matchCount == 5 && bonusMatch) {
            return 2; // 2등
        }
        if (matchCount == 5) {
            return 3; // 3등
        }
        if (matchCount == 4) {
            return 4; // 4등
        }
        if (matchCount == 3) {
            return 5; // 5등
        }

        return 0; // 당첨 안됨
    }

    private Integer calcLottoCount(Integer price) {
        // 1000원으로 딱 떨어지지 않는가?
        if (price % lottoPrice != 0) {
            throw new IllegalArgumentException("[ERROR] 구입 금액은 1,000원 단위로 입력해야 합니다.");
        }

        return price / lottoPrice;
    }

    private void checkRangeLotto(Integer[] lotto) {
        validateRange(lotto);
        validateDuplication(lotto);
    }

    private void validateRange(Integer[] lotto) {
        for (Integer number : lotto) {
            if (number < 1 || number > 45) {
                throw new IllegalArgumentException("[ERROR] 로또 번호는 1부터 45 사이의 숫자여야 합니다.");
            }
        }
    }

    private void validateDuplication(Integer[] lotto) {
        for (int i = 0; i < lotto.length; i++) {
            checkDuplicationFrom(lotto, i);
        }
    }

    private void checkDuplicationFrom(Integer[] lotto, int startIndex) {
        for (int j = startIndex + 1; j < lotto.length; j++) {
            if (lotto[startIndex].equals(lotto[j])) {
                throw new IllegalArgumentException("[ERROR] 로또 번호는 중복될 수 없습니다.");
            }
        }
    }

    private Integer[] validateWinningNumbers(String input) {
        String[] parts = input.split(",");

        if (parts.length != lottoBallCount) {
            throw new IllegalArgumentException("[ERROR] 당첨 번호는 " + lottoBallCount + "개여야 합니다.");
        }

        Integer[] numbers = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Integer.valueOf(parts[i].trim());
        }

        checkRangeLotto(numbers);
        return numbers;
    }

    private void validateBonusNumber(String input, Integer[] winningNumbers) {
        Integer bonusNumber = Integer.valueOf(input.trim());

        if (bonusNumber < lottoMinNum || bonusNumber > lottoMaxNum) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 " + lottoMinNum + "부터 " + lottoMaxNum + " 사이의 숫자여야 합니다.");
        }

        for (Integer number : winningNumbers) {
            if (number.equals(bonusNumber)) {
                throw new IllegalArgumentException("[ERROR] 보너스 번호는 당첨 번호와 중복될 수 없습니다.");
            }
        }
    }
}
