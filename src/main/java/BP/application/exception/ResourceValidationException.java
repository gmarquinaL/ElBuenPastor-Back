package BP.application.exception;

import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

public class ResourceValidationException extends  RuntimeException
{
    public ResourceValidationException(String message){
        super(message);
    }

    public <T> ResourceValidationException(String resourceName, Set<ConstraintViolation<T>> violations) {
        super(String.format("Not all constraints satisfied for %s: %s", resourceName,
                violations.stream().map( violation -> String.format("%s %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining(". "))));
    }
}
