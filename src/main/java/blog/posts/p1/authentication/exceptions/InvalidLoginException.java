package blog.posts.p1.authentication.exceptions;

import org.zalando.problem.Status;
import org.zalando.problem.violations.Violation;

import java.util.List;

public class InvalidLoginException extends BaseProblem {
    public InvalidLoginException(String reason, List<Violation> violations) {
        super(Status.UNAUTHORIZED, "Authentication", "InvalidLogin", reason, violations);
    }

    public InvalidLoginException(String reason, Violation violation) {
        super(Status.UNAUTHORIZED, "Authentication", "InvalidLogin", reason, List.of(violation));
    }

    public InvalidLoginException() {
        super(Status.UNAUTHORIZED, "Authentication", "InvalidLogin", "The email or password provided is incorrect");
    }
}
