import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { User } from '../../dtos/user';
import { AuthService } from '../../services/auth.service';
import CustomValidator from '../../custom-validator';


@Component({
    selector: 'app-user-create',
    templateUrl: './user-create.component.html',
    styleUrls: ['../login/login.component.scss', './user-create.component.scss']
})
export class UserCreateComponent implements OnInit {

    createForm: UntypedFormGroup;
    // After first submission attempt, form validation will start
    submitted = false;
    // Error flag
    error = false;
    errorMessage = '';
    // Success flag
    success = false;

    constructor(private formBuilder: UntypedFormBuilder, private userService: UserService, private router: Router,
                private authService: AuthService) {
        this.createForm = this.formBuilder.group({
            firstName: ['', [Validators.required]],
            lastName: ['', [Validators.required]],
            username: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
            password: ['', [Validators.required, Validators.minLength(8)]],
            passwordCheck: ['', [Validators.required]],
            role: ['', [Validators.required]]
        }, {validators: CustomValidator.match('password', 'passwordCheck')});
    }

    /**
     * Form validation will start after the method is called, additionally a CreateRequest will be sent
     */
    submitUser() {
        this.submitted = true;
        if (this.createForm.valid) {
            const createRequest: User = new User();
            createRequest.id = null;
            createRequest.firstName = this.createForm.controls.firstName.value;
            createRequest.lastName = this.createForm.controls.lastName.value;
            createRequest.email = this.createForm.controls.username.value;
            createRequest.password = this.createForm.controls.password.value;
            createRequest.role = this.createForm.controls.role.value;
            this.createUser(createRequest);
        } else {
            console.log('Invalid input');
        }
    }

    /**
     * Send creation data to the createService. If the creation was successfully, the user will be returned to the start page
     *
     * @param user creation data from the user create form
     */
    createUser(user: User) {
        console.log('Try to create user: ' + user.firstName + ' ' + user.lastName);
        this.userService.createUser(user).subscribe({
            next: () => {
                this.success = true;
                console.log('Successfully created user: ' + user.firstName + ' ' + user.lastName);
                setTimeout(() => {
                    this.success = false;
                    this.router.navigate(['']);
                }, 3000);
            },
            error: error => {
                console.log('Could not create new due to:');
                console.log(error);
                this.error = true;
                if (typeof error.error === 'object') {
                    this.errorMessage = error.error.error;
                } else {
                    this.errorMessage = error.error;
                }
            }
        });
    }

    /**
     * Error flag will be deactivated, which clears the error message
     */
    vanishError() {
        this.error = false;
    }

    /**
     * Success flag will be deactivated, which clears the success message
     */
    vanishSuccess() {
        this.success = false;
    }

    ngOnInit() {
        if (this.authService.getUserRole() !== 'ADMIN') {
            console.log('Logged in user is not admin');
            this.router.navigate(['']);
        }
    }

}
