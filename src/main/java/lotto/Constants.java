package lotto;

public enum Constants {
    LOTTO_PRICE(1000),
    MIN_LOTTO_NUM(1),
    MAX_LOTTO_NUM(45),
    LOTTO_RANDOM_BALL_COUNT(5),
    LOTTO_BONUS_BALL_COUNT(1),
    LOTTO_FIRST_PRIZE(2000000000),
    LOTTO_SECOND_PRIZE(30000000),
    LOTTO_THIRD_PRIZE(1500000),
    LOTTO_FOURTH_PRIZE(50000),
    LOTTO_FIFTH_PRIZE(5000);

    private final int value;

    Constants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
