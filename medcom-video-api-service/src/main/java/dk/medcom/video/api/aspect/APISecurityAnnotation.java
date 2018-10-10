package dk.medcom.video.api.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dk.medcom.video.api.context.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface APISecurityAnnotation {
	public UserRole[] value() default UserRole.UNDEFINED;

}