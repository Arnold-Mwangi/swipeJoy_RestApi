package blog.posts.p1.authentication.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.Violation;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseProblem extends AbstractThrowableProblem {
    private Status status;
    private String root;
    private String scope;
    private String reason;
    private List<Violation> violations;

    public BaseProblem(Status status, String root, String scope, String reason) {
        super(null, reason, status, null, null, null, null);
    }

    @Override
    public String getMessage() {
        return String.format("Root: %s, Scope: %s, Reason: %s, Violations: [%s]",
                root, scope, reason, violationsErrorMessage());
    }

    private String violationsErrorMessage() {
        return violations.stream()
                .map(violation -> violation.getField() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
    }
}
