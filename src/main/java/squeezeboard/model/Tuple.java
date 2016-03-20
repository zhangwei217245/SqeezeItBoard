package squeezeboard.model;

import java.util.Objects;

/**
 * Created by zhangwei on 3/20/16.
 */
public class Tuple<R, S, T> {

    private R first;

    private S second;

    private T third;

    public R getFirst() {
        return first;
    }

    public void setFirst(R first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public T getThird() {
        return third;
    }

    public void setThird(T third) {
        this.third = third;
    }

    public Tuple(R first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;
        return Objects.equals(first, tuple.first) &&
                Objects.equals(second, tuple.second) &&
                Objects.equals(third, tuple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
