package pl.agh.edu.wiet.clinic.model;

public final class ExaminationDTO {
    private final ExaminationType examinationType;
    private final String patientsSurname;

    public ExaminationDTO(ExaminationType examinationType, String patientsSurname) {
        this.examinationType = examinationType;
        this.patientsSurname = patientsSurname;
    }

    public ExaminationType getExaminationType() {
        return examinationType;
    }

    public String getPatientsSurname() {
        return patientsSurname;
    }
}
