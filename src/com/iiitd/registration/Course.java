package com.iiitd.registration;
import java.time.LocalDateTime;
import java.util.*;

import static com.iiitd.registration.Main.courseCatalog;
import static com.iiitd.registration.Main.userMap;

public class Course {
    public ArrayList<Student> students = new ArrayList<>();
    private final String title;
    public Professor professor;
    private final int semester;
    public int credits;
    private final ArrayList<String> prerequisites;
    private final ArrayList<String> syllabus;
    private HashMap<String, String> timings;
    private final String courseCode;
    private TA courseTA;
    private int classLimit = 10; //hard coded for simplicity but can be changed by prof
    private LocalDateTime dropDeadline;

    public int currClass=0;
    Admin admin = new Admin("random");

    public Course (String title, String prof, int semester, int credits, String courseCode, String... prerequisites) {
        this.title = title;
        for (User user : userMap.values()){
            if (user instanceof Professor) {
                if (Objects.equals(((Professor) user).name, prof)){
                    this.professor = (Professor) user;
                }
            }
        }
        if (this.professor == null){
            this.professor = new Professor(prof+"@mail.com", "pass"+prof, prof);
            admin.assignProf(courseCode,prof);
        }
        else{
            this.professor = Professor.findProfByName(prof);
            admin.assignProf(courseCode,prof);
        }
        this.semester = semester;
        this.credits = credits;
        this.prerequisites = new ArrayList<>(Arrays.asList(prerequisites));
        this.syllabus = new ArrayList<>();
        this.timings = new HashMap<>();
        this.courseCode = courseCode;
        LocalDateTime deadline = LocalDateTime.now().plusMinutes(7); //1 minute = 1 day here so deadline is one week
        this.setDropDeadline(deadline);
        courseCatalog.add(this);
    }

    private void setDropDeadline(LocalDateTime deadline) {
        this.dropDeadline=deadline;
    }

    public void setTimings(String day, String timing) {
        if (this.timings == null) {
            this.timings = new HashMap<>();
        }
        this.timings.put(day, timing);
    }

    public void addTiming(String day, String timeSlot) {
        timings.put(day, timeSlot);
    }

    public void removeTiming(String day) {
        if (!timings.containsKey(day)){
            System.out.println("Invalid input");
            return;
        }
        timings.remove(day);
    }

    public static Course findCourseByCode(String courseCode) {
        for (Course c: courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                return c;
            }
        }
        return null;
    }

    public void addPrerequisite(String prerequisite) {
        this.prerequisites.add(prerequisite);
    }

    public ArrayList<String> getPrerequisites() {
        if (prerequisites.isEmpty()) {
            return null;
        }
        return prerequisites;
    }

    public String getProfessor() {
        return this.professor.name;
    }

    public int getSemester() {
        return this.semester;
    }

    public int getCredits() {
        return this.credits;
    }

    public void addToSyllabus(String topic) {
        this.syllabus.add(topic);
    }

    public String getSyllabus() {
        if (syllabus.isEmpty()) {
            return ("Syllabus has not yet been set");
        }
        return String.join(", ",syllabus);
    }

    public HashMap<String,String> getTimings() {
        return timings;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public static List<Course> getCourseCatalog() {
        return courseCatalog;
    }

    ////////////////////////////////////////////////

    public TA getCourseTA() {
        return courseTA;
    }

    public void setCourseTA(TA courseTA) {
        this.courseTA = courseTA;
    }

    public void setClassLimit(int classLimit) {
        this.classLimit = classLimit;
    }

    public int getClassLimit() {
        return classLimit;
    }

    public LocalDateTime getDropDeadline() {
        return dropDeadline;
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            if (this != null) {
                if (!student.getCurrCourses().contains(this))
                    student.registerForCourse(this.getCourseCode());
            }
        }
    }
}