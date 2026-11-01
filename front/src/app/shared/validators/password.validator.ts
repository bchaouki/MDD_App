import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Même règle que l'annotation @StrongPassword de l'API. */
export const PASSWORD_PATTERN = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^\w\s]).{8,}$/;

export const PASSWORD_HINT =
  'Au moins 8 caractères dont un chiffre, une minuscule, une majuscule et un caractère spécial';

/** Une valeur vide est acceptée : combiner avec Validators.required si le champ est obligatoire. */
export function strongPasswordValidator(): ValidatorFn {
  return (control: AbstractControl<string | null>): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null;
    }
    return PASSWORD_PATTERN.test(value) ? null : { strongPassword: true };
  };
}
