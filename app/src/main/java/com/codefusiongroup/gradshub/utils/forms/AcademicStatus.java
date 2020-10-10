package com.codefusiongroup.gradshub.utils.forms;

import java.util.ArrayList;
import java.util.List;

public class AcademicStatus {

    // constructor private so that only one instance of AcademicStatus object is created and no other
    // class can instantiate it directly
    private AcademicStatus() { }

    private static AcademicStatus instance;
    private static List<String> academicStatusesList;
    private static final String statusHintPlaceHolder = "Select your academic status here";

    // singleton pattern
    public static AcademicStatus getInstance() {
        if (instance == null) {
            academicStatusesList = new ArrayList<>();
            initAcademicStatuses();
            instance = new AcademicStatus();
        }
        return instance;
    }

    private static void initAcademicStatuses() {
        academicStatusesList.add(statusHintPlaceHolder);
        academicStatusesList.add("Honours");
        academicStatusesList.add("Masters");
        academicStatusesList.add("PhD");
    }

    public String getAcademicStatus(int position) {
        return academicStatusesList.get(position);
    }

}
