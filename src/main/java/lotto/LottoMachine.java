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
            // 랜덤 번호 생성 (1-45 중 6개)
            List<Integer> numbers = Randoms.pickUniqueNumbersInRange(lottoMinNum, lottoMaxNum, lottoCount);
            Collections.sort(numbers);

            // myLotto 배열에 저장
            for (Integer number : numbers) {
                myLotto[index++] = number;
            }

            // 발행된 로또 번호 출력
            System.out.print(numbers);
            if (i < count - 1) {
                System.out.println();
            }
        }
        System.out.println(); // 마지막 줄바꿈
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

    private Integer calcLottoCount(Integer price) {
        // 1000원으로 딱 떨어지지 않는가?
        if (price % lottoPrice != 0) {
            throw new IllegalArgumentException("[ERROR] 구입 금액은 1,000원 단위로 입력해야 합니다.");
        }

        return price / lottoPrice;
    }

    private void checkRangeLotto(Integer[] lotto) {
        // 1. 범위 검증 (1-45)
        for (Integer number : lotto) {
            if (number < 1 || number > 45) {
                throw new IllegalArgumentException("[ERROR] 로또 번호는 1부터 45 사이의 숫자여야 합니다.");
            }
        }

        // 2. 중복 검증
        for (int i = 0; i < lotto.length; i++) {
            for (int j = i + 1; j < lotto.length; j++) {
                if (lotto[i].equals(lotto[j])) {
                    throw new IllegalArgumentException("[ERROR] 로또 번호는 중복될 수 없습니다.");
                }
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
