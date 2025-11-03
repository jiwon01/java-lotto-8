package lotto;

import camp.nextstep.edu.missionutils.*;

public class LottoMachine {
    private final Integer lottoPrice = Constants.LOTTO_PRICE.getValue();

    private Integer[] myLotto;

    public void start() {
        Integer lottoCount = buyLotto();
    }

    /**
     * 결제 및 로또 개수 반환
     */
    private Integer buyLotto() {
        // 1. 구입 금액 입력
        System.out.println("구입금액을 입력해 주세요.");
        String paidPrice = Console.readLine();

        Integer lottoCount = calcLottoCount(Integer.valueOf(paidPrice));

        return lottoCount;
    }

    private Integer calcLottoCount(Integer price) {
        // 1000원으로 딱 떨어지지 않는가?
        if (price % lottoPrice != 0) {
            throw new IllegalArgumentException("[ERROR] 구입 금액은 1,000원 단위로 입력해야 합니다.");
        }

        return price / lottoPrice;
    }
}
