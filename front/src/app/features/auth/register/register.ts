import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { Router } from '@angular/router';
import { finalize, switchMap } from 'rxjs';
import { errorMessage } from '../../../core/models/api-error';
import { SessionService } from '../../../core/services/session.service';
import { BackButton } from '../../../shared/components/back-button/back-button';
import { FormError } from '../../../shared/components/form-error/form-error';
import {
  PASSWORD_HINT,
  strongPasswordValidator,
} from '../../../shared/validators/password.validator';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInput, MatButton, BackButton, FormError],
  templateUrl: './register.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly session = inject(SessionService);
  private readonly router = inject(Router);

  protected readonly passwordHint = PASSWORD_HINT;
  readonly form = inject(NonNullableFormBuilder).group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    password: ['', [Validators.required, strongPasswordValidator()]],
  });
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.error.set(null);
    this.authService
      .register(this.form.getRawValue())
      .pipe(
        switchMap(({ token }) => this.session.logIn(token)),
        finalize(() => this.submitting.set(false)),
      )
      .subscribe({
        next: () => this.router.navigateByUrl('/feed'),
        error: (err: unknown) => this.error.set(errorMessage(err)),
      });
  }
}
