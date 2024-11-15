///////////////////////////////////////
package com.iiitd.registration;

import java.util.ArrayList;

import static com.iiitd.registration.Main.userMap;

public class TA extends Student{
    private ArrayList<Student> students;
    public Course course;
    private Professor prof;

    public TA(String email, String password, int semester, String name, String courseCode) {
        super(email, password, semester, name);
        this.students = new ArrayList<>();
        this.course=Course.findCourseByCode(courseCode);
        if (course!=null) {
            course.setCourseTA(this);
            if (course.professor!=null){
                students=course.students;
            }
        }
    }

    public void viewAssignedStudents(String courseCode) {
        if (students.isEmpty()){
            System.out.println("No students assigned to you yet");
            return;
        }
        System.out.println("Students enrolled in course " + courseCode + ":");
        System.out.printf("%-20s | %-20s | %-5s%n", "Name", "Email", "CGPA");

        for (Student student : students) {
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


}
