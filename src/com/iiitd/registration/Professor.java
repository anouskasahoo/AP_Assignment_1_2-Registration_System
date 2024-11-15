package com.iiitd.registration;

import java.util.Objects;

import static com.iiitd.registration.Main.feedbacks;
import static com.iiitd.registration.Main.userMap;

public class Professor extends User {
    public String name;
    public Course course;

    public Professor(String email, String password, String name) {
        super(email, password);
        this.name = name;
        userMap.put(email, this);
        course = null;
    }

    public static Professor findProfByName(String name){
        Professor professor = null;
        boolean profFound = false;
        for (User user : userMap.values()) {
            if (user instanceof Professor prof) {
                if (Objects.equals(prof.name, name)){
                    profFound = true;
                    professor = prof;
                }
            }
        }
        if (!profFound) {
            System.out.println("Invalid email, prof not found");
            return null;
        }
        return professor;
    }

    public void getCourseDetails(String name){
        for (Course c: Course.getCourseCatalog()){
            if (Objects.equals(c.getProfessor(), name)) {
                System.out.println(c.getTitle()+" | "+c.getCourseCode());
                System.out.println("Semester: "+c.getSemester()+" | "+"Credits offered: "+c.getCredits());
                System.out.println("Teaching Assistant: "+course.getCourseTA().name);
                System.out.println("Class Limit: "+course.getClassLimit());
                return;
            }
        }
        System.out.println("No course assigned yet");
    }

    public void viewStudentsInCourse(String courseCode) {
        System.out.println("Students enrolled in course " + courseCode + ":");
        System.out.printf("%-20s | %-20s | %-5s%n", "Name", "Email", "CGPA");

        for (Student student : course.students) {
            for (Course course : student.getCurrCourses()) {
                if (course.getCourseCode().equals(courseCode)) {
                    System.out.printf("%-20s | %-20s | %-5.2f%n", student.name, student.getEmail(), student.getCGPA());
                }
            }
        }
    }

    public void uploadScore(String studentEmail, String courseCode, int score) {
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

        student.addCourseScore(courseCode, (double) score);
        System.out.println("Score uploaded successfully.");

    }

    ///////////////////////////////////////////////

    public void assignTA(String name){
        if (course==null){
            return;
        }
        for (Student st: course.students){
            if (Objects.equals(st.name, name)){
                if (st.getPrevCourses().contains(course)||st.getCurrCourses().contains(course)) {
                    TA ta = new TA(st.getEmail(), st.password, st.getSemester(), st.name, course.getCourseCode());
                    userMap.put(ta.getEmail(), ta);
                    System.out.println("TA has been assigned\n");
                    return;
                }
            }
        }
        System.out.println("Invalid name\n");
    }

    public void viewFeedbacks(){
        if (this.course==null){
            System.out.println("No course assigned yet");
            return;
        }
        System.out.println("Feedbacks- \n");
        for (Feedback f : feedbacks){
            if (f.getCourse()==this.course){
                System.out.println(f.getFeedback() + " | " + f.getDesc() + "\n");
            }
        }
    }
}
