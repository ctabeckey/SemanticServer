import com.paypal.credit.context.annotations.NanoBean;
import com.paypal.credit.context.annotations.NanoInject;

/**
 * Created by cbeckey on 2/7/17.
 */
@NanoBean(identifier = "ts1")
public class TestSubject {
    final TestSubjectTwo two;

    public TestSubject(final @NanoInject(identifier = "ts2") TestSubjectTwo two) {
        this.two = two;
    }

    public TestSubjectTwo getTwo() {
        return two;
    }
}
