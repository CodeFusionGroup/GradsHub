package com.codefusiongroup.gradshub.authentication.registration;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.BR;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.utils.forms.AcademicStatus;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;
import com.codefusiongroup.gradshub.utils.forms.RegisterForm;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.common.repositories.UserRepositoryImpl;


public class RegisterViewModel extends ObservableViewModel {

    private UserRepositoryImpl repository;
    private final AcademicStatus academicStatuses = AcademicStatus.getInstance();

    public RegisterViewModel() {
        repository = UserRepositoryImpl.getInstance();
    }

    // properties bound to xml attributes initialised with empty strings
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String phoneNo = "";
    private String academicStatus = "";
    private String password = "";
    private String confirmPassword = "";
    private int selectedItemPosition = 0;

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Resource<String>> registrationResponse;
    private MutableLiveData<Boolean> isTokenGenerated = new MutableLiveData<>(true);


    @Bindable
    public String getFirstName() { return this.firstName; }

    @Bindable
    public String getLastName() { return this.lastName; }

    @Bindable
    public String getEmail() { return this.email; }

    @Bindable
    public String getPhoneNo() { return this.phoneNo; }

    @Bindable
    public String getPassword() { return this.password; }

    @Bindable
    public String getConfirmPassword() { return this.confirmPassword; }

    @Bindable
    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
        this.academicStatus = academicStatuses.getAcademicStatus(selectedItemPosition);
        notifyPropertyChanged(BR.selectedItemPosition);
    }

    public void setFirstName(String firstName) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.firstName.equals(firstName) ) {
            return;
        }
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    public void setLastName(String lastName) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.lastName.equals(lastName) ) {
            return;
        }
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }

    public void setEmail(String email) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.email.equals(email) ) {
            return;
        }
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public void setPhoneNo(String phoneNo) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.phoneNo.equals(phoneNo) ) {
            return;
        }
        this.phoneNo = phoneNo.trim();
        notifyPropertyChanged(BR.phoneNo);
    }

    public void setPassword(String password) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.password.equals(password) ) {
            return;
        }
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void setConfirmPassword(String confirmPassword) {
        // Avoids infinite loops (associated with two way data binding)
        if ( this.confirmPassword.equals(confirmPassword) ) {
            return;
        }
        this.confirmPassword = confirmPassword;
        notifyPropertyChanged(BR.confirmPassword);
    }


    public void onSubmitClicked() {

        RegisterForm registerForm = new RegisterForm.RegisterFormBuilder()
                .withFirstName(firstName.trim())
                .withLastName(lastName.trim())
                .withEmail(email.trim())
                .withPhoneNo(phoneNo)
                .withAcademicStatus(academicStatus)
                .withPassword(password.trim())
                .withConfirmPassword(confirmPassword.trim())
                .build();

        if ( FormValidator.getInstance().isFormStateValid(registerForm) ) {
            String token = UserPreferences.getInstance().getToken(GradsHubApplication.getContext());
            if ( !token.equals("no token set") ) {
                repository.registerUser( new User(firstName.trim(), lastName.trim(), email.trim(), phoneNo.trim(), academicStatus.trim(), password.trim(), token) );
            }
            else {
                isTokenGenerated.setValue(false);
            }
        }

    }


    public LiveData<Boolean> getTokenStatus() {
        return isTokenGenerated;
    }

    public LiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = repository.getIsLoading();
        }
        return isLoading;
    }

    public LiveData<Resource<String>> getRegisterResponse() {
        if (registrationResponse == null) {
            registrationResponse = repository.getRegisterResponse();
        }
        return registrationResponse;
    }

}
