package com.codefusiongroup.gradshub.authentication.login;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.BR;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.repositories.UserRepositoryImpl;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.codefusiongroup.gradshub.utils.forms.LoginForm;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;
import com.codefusiongroup.gradshub.utils.api.Resource;


public class LoginViewModel extends ObservableViewModel {

    private UserRepositoryImpl repository;

    public LoginViewModel() {
        repository = UserRepositoryImpl.getInstance();
    }

    // properties bound to login xml file initialised with empty strings
    private String email = "";
    private String password = "";

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Resource<User>> loginResponse;

    // gets email and password properties from the auto-generated BR class to identify which field has changed.
    @Bindable
    public String getEmail() { return this.email; }

    @Bindable
    public String getPassword() { return this.password; }

    public void setEmail(String email) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.email.equals(email) ) {
            return;
        }
        this.email = email.trim();
        notifyPropertyChanged(BR.email);
    }

    public void setPassword(String password) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.password.equals(password) ) {
            return;
        }
        this.password = password.trim();
        notifyPropertyChanged(BR.password);
    }


    public void onLoginClicked() {

        LoginForm loginForm = new LoginForm.LoginFormBuilder()
                .withEmail(email)
                .withPassword(password)
                .build();

        if ( FormValidator.getInstance().isFormStateValid(loginForm) ) {
            repository.loginUser(email, password);
        }
    }

    public LiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = repository.getIsLoading();
        }
        return isLoading;
    }

    public LiveData<Resource<User>> getLoginResponse() {
        if (loginResponse == null) {
            loginResponse = repository.getLoginResponse();
        }
        return loginResponse;
    }

    public void onLoginSuccessful() {
        if (loginResponse != null) {
            loginResponse.setValue(null);
        }
    }

}
