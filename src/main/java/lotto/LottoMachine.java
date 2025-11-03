package lotto;

import camp.nextstep.edu.missionutils.*;

import java.util.Collections;
import java.util.List;

public class LottoMachine {
    private final Integer lottoPrice = Constants.LOTTO_PRICE.getValue();
    private final Integer lottoMinNum = Constants.MIN_LOTTO_NUM.getValue();
    private final Integer lottoMaxNum = Constants.MAX_LOTTO_NUM.getValue();
    private final Integer lottoCount = Constants.LOTTO_RANDOM_BALL_COUNT.getValue() + Constants.LOTTO_BONUS_BALL_COUNT.getValue();


    private Integer[] myLotto;

    public void start() {
        Integer lottoCount = buyLotto();

        getLottoNumbers(lottoCount);
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
}
