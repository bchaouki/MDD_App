package com.openclassrooms.mddapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class) // Fait le lien avec la logique métier, indique que cette contrainte est validée par PasswordValidator
@Target({ ElementType.FIELD }) // S'applique uniquement sur les variables/champs, restreint l’utilisation de l’annotation aux champs.
@Retention(RetentionPolicy.RUNTIME) // rend l’annotation disponible à l’exécution, indispensable pour la validation.
public @interface Password {

    String message() default "Invalid password"; // Message d'erreur par défaut

    Class<?>[] groups() default {}; // Requis par la spécification Bean Validation

    Class<? extends Payload>[] payload() default {}; // Requis pour transporter des métadonnées
}
