package com.iiitd.registration;
import java.util.Objects;

import static com.iiitd.registration.Main.*;

public class Admin extends User {
    static final String adminPassword = "AdminEve"; //heh get it? adam n eve, admin eve.. (lame sorry)

    public Admin (String email) {
        super (email, adminPassword);
        userMap.put(email,this);
    }

    public boolean adminLog (String password){
        return Objects.equals(password, adminPassword);
    }

    public void viewCourses() {
        System.out.println("Course Catalog:");
        for (Course course : courseCatalog) {
            System.out.println(course.getTitle() + " | " + course.getCourseCode() + " | Semester: " + course.getSemester());
        }
    }

    public void addCourse(String title, String professor, int semester, int credits, String courseCode, String... prerequisites) {
        Course newCourse = new Course(title, professor, semester, credits, courseCode, prerequisites);
        courseCatalog.add(newCourse);
        System.out.println("Course added: " + newCourse.getCourseCode());
    }

    public void deleteCourse(String courseCode) {
        Course toDelete = null;
        for (Course course : courseCatalog) {
            if (course.getCourseCode().equals(courseCode)) {
                toDelete = course;
                break;
            }
        }
        if (toDelete != null) {
            courseCatalog.remove(toDelete);
            System.out.println("Course deleted: " + toDelete.getCourseCode());
        } else {
            System.out.println("Course not found.");
        }
    }

    public void viewStudentRecords() {
        for (User user : userMap.values()) {
            if (user instanceof Student student) {
                System.out.println("Name: " + student.name + " | Email: " + student.getEmail() + " | Semester: " + student.getSemester() + " | CGPA: " + student.getCGPA());
            }
        }

    }

    public void viewProfRecords() {
        for (User user : userMap.values()) {
            if (user instanceof Professor prof) {
                System.out.println("\nName: " + prof.name + " | Email: " + prof.getEmail());
                prof.getCourseDetails(prof.name);
            }
        }

    }

    public void uploadScore(String studentEmail, String courseCode, double score) {
        Student student = (Student) userMap.get(studentEmail);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        Course course = Course.findCourseByCode(courseCode);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        if (!student.getCurrCourses().contains(course)) {
            System.out.println("Student is not registered for this course.");
            return;
        }

        student.addCourseScore(courseCode, score);
        System.out.println("Score uploaded successfully.");

    }

    public void assignProf(String code, String name) {
        Course course = null;
        boolean courseFound = false;
        for (Course c: courseCatalog){
            if (Objects.equals(c.getCourseCode(), code)){
                course = c;
                courseFound = true;
                break;
            }
        }
        if (!courseFound){
            //System.out.println("Invalid course code");
            return;
        }


        boolean profFound = false;

        for (User user : userMap.values()) {
            if (user instanceof Professor) {
                if (Objects.equals(((Professor) user).name, name)) {
                    profFound = true;
                    break;
                }
            }
        }

        if (!profFound) {
            System.out.println("Invalid professor name (not found in records)");
            return;
        }

        for (User user : userMap.values()) {
            if (user instanceof Professor prof) {
                if (Objects.equals(prof.name, name)){
                    prof.course= course ;
                    course.professor = prof;
                }
            }
        }
        System.out.println(name + " has been assigned to course " + code);
    }

    public void viewComplaints(){
        for (Complaint c : complaints){
            System.out.println(c.getComplaintNumber() + ". " + c.getFiledBy().name + " | " + c.getComplaint() + " | "  + c.isResolved());
        }
    }

    public void viewComplaintsIndividual(String name){
        for (Complaint c : complaints){
            if (Objects.equals(c.getFiledBy().name, name)) {
                System.out.println(c.getComplaintNumber() + ". " + c.getFiledBy().name + " | " + c.getComplaint() + " | " + c.isResolved());
            }
        }
    }

    public void changeStatus(int num, String name) {
        for (Complaint c : complaints){
            if ((Objects.equals(c.getFiledBy().name, name))&&(Objects.equals(c.getComplaintNumber(), num))) {
                c.changeStatus();
            }
        }
    }

    public void viewComplaintsAccToResolution(boolean b) {
        for (Complaint c : complaints){
            if (c.isResolved() == b) {
                System.out.println(c.getComplaintNumber() + ". " + c.getFiledBy().name + " | " + c.getComplaint() + " | " + c.isResolved());
            }
        }
    }

    public void completeStudentCourse(String email, String code){
        Student student = null;
        for (User user : userMap.values()) {
            if (user instanceof Student st) {
                if (Objects.equals(st.email, email)){
                    student = st;
                }
            }
        }
        if (student == null) {
            System.out.println("Invalid email, student not found");
            return;
        }

        student.addPrevCourse(code);
        student.dropCourse(code);
        student.updateSGPA();
        student.updateCGPA();
    }

    public void updateSemester(String email, int semester){
        if (semester>8 || semester <1) {
            System.out.println("Invalid semester, enter a value between 1 and 8 (inclusive): ");
            return;
        }
        boolean studentFound = false;
        Student student = null;
        for (User user : userMap.values()) {
            if (user instanceof Student st) {
                if (Objects.equals(st.email, email)){
                    studentFound = true;
                    student = st;
                }
            }
        }
        if (!studentFound) {
            System.out.println("Invalid email, student not found");
            return;
        }

        student.setSemester(semester);
    }

    /////////////////////////////

    public void viewFeedbacks(){
        if (feedbacks.isEmpty()){
            System.out.println("No feedbacks yet\n");
            return;
        }
        System.out.println("Course | Feedback");
        for (Feedback f:feedbacks){
            System.out.println(f.getCourse().getCourseCode() + " | " + f.getFeedback() + "\n");
        }
    }
}
