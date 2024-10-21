package blog.posts.p1.authentication.exceptions;

import org.zalando.problem.violations.Violation;

import java.util.Arrays;
import java.util.List;

public class UserAuthenticationFailed extends InvalidInputProblem{
    public UserAuthenticationFailed(String reason, Violation violation) {
        super(UserProblemScope.USER, UserProblemScope.AUTHENTICATION, reason, Arrays.asList(violation));

    }

    public UserAuthenticationFailed(String reason, List<Violation> violations) {
        super(UserProblemScope.USER, UserProblemScope.AUTHENTICATION, reason, violations);

    }
}
