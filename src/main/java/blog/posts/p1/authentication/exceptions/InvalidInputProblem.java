package blog.posts.p1.authentication.exceptions;

import lombok.Data;
import org.zalando.problem.Status;
import org.zalando.problem.violations.Violation;

import java.util.Arrays;
import java.util.List;

@Data
public class InvalidInputProblem extends BaseProblem {

    public InvalidInputProblem(String root, String scope, String reason, Violation violation) {
        super(Status.BAD_REQUEST, root, scope, reason, Arrays.asList(violation));
    }

    public InvalidInputProblem(String root, String scope, String reason, List<Violation> violations) {
        super(Status.BAD_REQUEST, root, scope, reason, violations);
    }
}
