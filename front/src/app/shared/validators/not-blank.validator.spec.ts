import { FormControl } from '@angular/forms';
import { notBlankValidator } from './not-blank.validator';

describe('notBlankValidator', () => {
  it('rejects blank values', () => {
    expect(notBlankValidator()(new FormControl('   '))).toEqual({ required: true });
    expect(notBlankValidator()(new FormControl(''))).toEqual({ required: true });
  });

  it('accepts text', () => {
    expect(notBlankValidator()(new FormControl(' ok '))).toBeNull();
  });
});
