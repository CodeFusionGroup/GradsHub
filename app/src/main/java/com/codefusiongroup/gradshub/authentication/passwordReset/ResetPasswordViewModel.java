package com.codefusiongroup.gradshub.authentication.passwordReset;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.BR;
import com.codefusiongroup.gradshub.common.repositories.UserRepositoryImpl;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;
import com.codefusiongroup.gradshub.utils.api.Resource;


public class ResetPasswordViewModel extends ObservableViewModel {

    private UserRepositoryImpl repository;

    public ResetPasswordViewModel() {
        repository = UserRepositoryImpl.getInstance();
    }

    private String email = "";

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Resource<String>> passwordResetResponse;

    // gets email and password properties from the BR class to identify which field has changed.
    @Bindable
    public String getEmail() { return this.email; }

    public void setEmail(String email) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.email.equals(email) ) {
            return;
        }
        this.email = email.trim();
        notifyPropertyChanged(BR.email);
    }

    public void onRequestResetClicked() {
        if ( FormValidator.getInstance().isValidEmail(email) ) {
            repository.requestPasswordReset(email);
        }
    }

    public LiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = repository.getIsLoading();
        }
        return isLoading;
    }

    public LiveData<Resource<String>> getPasswordResetResponse() {
        if (passwordResetResponse == null) {
            passwordResetResponse = repository.getPasswordResetResponse();
        }
        return passwordResetResponse;
    }

    public void onRequestResetSuccessful() {
        passwordResetResponse.setValue(null);
    }

}
