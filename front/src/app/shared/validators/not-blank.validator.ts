import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Refuse une valeur vide ou composée uniquement d'espaces. */
export function notBlankValidator(): ValidatorFn {
  return (control: AbstractControl<string | null>): ValidationErrors | null =>
    control.value && control.value.trim().length > 0 ? null : { required: true };
}
