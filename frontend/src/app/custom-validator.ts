import {AbstractControl, ValidatorFn} from '@angular/forms';

/**
 * This class is used to check if two given strings of an AbstractControl match
 *
 */
export default class CustomValidator {
  static match(controlName: string, matchControlName: string): ValidatorFn {
    return (controls: AbstractControl) => {
      const control = controls.get(controlName);
      const matchControl = controls.get(matchControlName);

      if (!matchControl?.errors && control?.value !== matchControl?.value) {
        matchControl?.setErrors({
          matching: {
            actualValue: matchControl?.value,
            requiredValue: control?.value
          }
        });
        return { matching: true };
      }
      return null;
    };
  }
}
