package com.codefusiongroup.gradshub.utils.validations;

import java.util.HashMap;
import java.util.Map;

public class InputValidationErrors {

    private InputValidationErrors() {
        // must explicitly define as private so cannot be instantiated directly by another class
        // to ensure same object is returned
    }

    private static InputValidationErrors instance;
    // singleton pattern
    public static InputValidationErrors getInstance() {
        if (instance == null) {
            errorTypes = new HashMap<>();
            initErrors(); // will only be called once, the first time ValidationHandler is required
            instance = new InputValidationErrors();
        }
        return instance;
    }


    private static Map<String, String> errorTypes;
    public static final String EMPTY_FIELD = "EMPTY_FIELD";
    public static final String INVALID_EMAIL_FORMAT = "INVALID_EMAIL_FORMAT";
    public static final String INVALID_CONTACT_NO = "INVALID_CONTACT_NO";
    public static final String INVALID_ACADEMIC_STATUS = "ACADEMIC_STATUS";
    public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
    public static final String PASSWORD_SHORT = "PASSWORD_SHORT";

    private static void initErrors() {
        errorTypes.put(EMPTY_FIELD, "Field can't be empty.");
        errorTypes.put(INVALID_EMAIL_FORMAT, "Invalid email address, check that your email address is entered correctly.");
        errorTypes.put(INVALID_CONTACT_NO, "Invalid phone number.");
        errorTypes.put(INVALID_ACADEMIC_STATUS, "Please select valid academic status.");
        errorTypes.put(PASSWORD_SHORT, "Password must be a minimum of 5 character.");
        errorTypes.put(PASSWORD_MISMATCH, "Password does not match the above entered password.");
    }

    public String getError(String errorKey) {
        return errorTypes.get(errorKey);
    }

}
