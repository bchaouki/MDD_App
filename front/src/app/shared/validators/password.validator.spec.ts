import { FormControl } from '@angular/forms';
import { strongPasswordValidator } from './password.validator';

describe('strongPasswordValidator', () => {
  const validate = (value: string) => strongPasswordValidator()(new FormControl(value));

  it('accepts a strong password', () => {
    expect(validate('Passw0rd!')).toBeNull();
  });

  it('rejects passwords missing a rule', () => {
    for (const weak of ['Pa0!', 'password1!', 'PASSWORD1!', 'Password!', 'Password1']) {
      expect(validate(weak), weak).toEqual({ strongPassword: true });
    }
  });

  it('leaves empty values to Validators.required', () => {
    expect(validate('')).toBeNull();
  });
});
