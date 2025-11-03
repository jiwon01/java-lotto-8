package lotto;

import camp.nextstep.edu.missionutils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LottoMachine {
    private final Integer lottoPrice = Constants.LOTTO_PRICE.getValue();
    private final Integer lottoMinNum = Constants.MIN_LOTTO_NUM.getValue();
    private final Integer lottoMaxNum = Constants.MAX_LOTTO_NUM.getValue();
    private final Integer lottoBallCount = Constants.LOTTO_RANDOM_BALL_COUNT.getValue();

    private List<Lotto> myLottos;
    private List<Integer> winnerBall;
    private Integer winnerBonusBall;

    public void start() {
        try {
            Integer lottoCount = buyLotto();

            getLottoNumbers(lottoCount);

            enterWinningNumbers();

            printWinningStatistics(lottoCount);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 1. 결제 및 로또 개수 반환
     */
    private Integer buyLotto() {
        // 1. 구입 금액 입력
        System.out.println("구입금액을 입력해 주세요.");
        String paidPrice = Console.readLine();

        System.out.println(); // 줄바꿈용

        try {
            return calcLottoCount(Integer.valueOf(paidPrice));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 구입 금액은 숫자여야 합니다.");
        }
    }

    /**
     * 2. 로또 번호 랜덤 발행
     * @param count 로또 개수
     */
    private void getLottoNumbers(Integer count) {
        myLottos = new ArrayList<>();

        System.out.println(count + "개를 구매했습니다.");

        for (int i = 0; i < count; i++) {
            generateAndPrintOneLotto(i, count);
        }
        System.out.println(); // 마지막 줄바꿈
    }

    private void generateAndPrintOneLotto(int currentIndex, int totalCount) {
        // 랜덤 번호 생성 (1-45 중 6개)
        List<Integer> numbers = new ArrayList<>(Randoms.pickUniqueNumbersInRange(lottoMinNum, lottoMaxNum, lottoBallCount));
        Collections.sort(numbers);

        // Lotto 객체 생성 및 저장
        Lotto lotto = new Lotto(numbers);
        myLottos.add(lotto);

        // 발행된 로또 번호 출력
        printLottoNumbers(numbers, currentIndex, totalCount);
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
        List<Integer> winningNumbers = validateWinningNumbers(winningBallNumbers);

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

        for (Lotto lotto : myLottos) {
            int rank = checkWinningRank(lotto);
            if (rank > 0) {
                rankCount[rank]++;
            }
        }
        return rankCount;
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

    private int checkWinningRank(Lotto lotto) {
        int matchCount = lotto.countMatchingNumbers(winnerBall);
        boolean bonusMatch = lotto.hasNumber(winnerBonusBall);

        return determineRank(matchCount, bonusMatch);
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
        // 0원 이하인가?
        if (price <= 0) {
            throw new IllegalArgumentException("[ERROR] 구입 금액은 0보다 커야 합니다.");
        }
        // 1000원으로 딱 떨어지지 않는가?
        if (price % lottoPrice != 0) {
            throw new IllegalArgumentException("[ERROR] 구입 금액은 1,000원 단위로 입력해야 합니다.");
        }

        return price / lottoPrice;
    }

    private List<Integer> validateWinningNumbers(String input) {
        String[] parts = input.split(",");

        if (parts.length != lottoBallCount) {
            throw new IllegalArgumentException("[ERROR] 당첨 번호는 " + lottoBallCount + "개여야 합니다.");
        }

        List<Integer> numbers = new ArrayList<>();
        try {
            for (String part : parts) {
                numbers.add(Integer.valueOf(part.trim()));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 당첨 번호는 숫자여야 합니다.");
        }

        // Lotto 객체를 통해 검증 (범위, 중복 체크)
        new Lotto(numbers);
        return numbers;
    }

    private void validateBonusNumber(String input, List<Integer> winningNumbers) {
        Integer bonusNumber;
        try {
            bonusNumber = Integer.valueOf(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 숫자여야 합니다.");
        }

        if (bonusNumber < lottoMinNum || bonusNumber > lottoMaxNum) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 " + lottoMinNum + "부터 " + lottoMaxNum + " 사이의 숫자여야 합니다.");
        }

        if (winningNumbers.contains(bonusNumber)) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 당첨 번호와 중복될 수 없습니다.");
        }
    }
}
