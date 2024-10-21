package blog.posts.p1.authentication.exceptions;

import org.zalando.problem.violations.Violation;

import java.util.Arrays;
import java.util.List;

public class UserInvalidInputProblem extends InvalidInputProblem {

    public UserInvalidInputProblem(String reason, List<Violation> violations) {
        super(UserProblemScope.USER, UserProblemScope.USER_INVALID_INPUT,reason, violations);
    }

    public UserInvalidInputProblem(String reason, Violation violation) {
        super(UserProblemScope.USER, UserProblemScope.USER_INVALID_INPUT,reason, Arrays.asList(violation));
    }
}
