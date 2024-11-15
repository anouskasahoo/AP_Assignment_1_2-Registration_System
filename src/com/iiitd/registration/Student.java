package com.iiitd.registration;
import java.time.LocalDateTime;
import java.util.*;

import static com.iiitd.registration.Main.*;

public class Student extends User {
    public String name;
    private int semester;
    private int credits;
    private final int currCredits;
    private final ArrayList<Course> prevCourses;
    private final ArrayList<Course> currCourses;
    private Double cgpa = 0.0;
    private Double sgpa = 0.0;
    private ArrayList<ArrayList<String>> schedule;
    private final Map<String, Double> courseScores = new HashMap<>();


    public Student(String email, String password, int semester, String name) {
        super(email, password);
        this.name = name;
        this.semester = semester;
        this.credits = 0;
        this.currCredits = 0;
        this.prevCourses = new ArrayList<>();
        this.currCourses = new ArrayList<>();
        this.schedule = new ArrayList<>();
        userMap.put(email, this);
    }

    public void dropCourse(String code){

        int ind = -1;
        for (Course c : currCourses) {
            if (c.getCourseCode().equals(code)) {
                ind = currCourses.indexOf(c);
                break;
            }
        }

        if (ind != -1) {
            Course course = currCourses.get(ind);

            if (LocalDateTime.now().isAfter(course.getDropDeadline())) {
                throw new DropDeadlinePassedException("Cannot drop the course. The deadline has passed.");
            }

            course.currClass--;
            currCourses.remove(ind);
            updateSchedule();
            System.out.println("Course dropped successfully.");
        } else {
            System.out.println("Course not found in current courses.");
        }
    }

    public static void viewAvailableCourses(Student student) {
        if (Course.getCourseCatalog().isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        boolean printed = false;
        for (Course course : Course.getCourseCatalog()) {
            if (student.semester>=course.getSemester()) {
                printed = true;
                System.out.println("Course Code: " + course.getCourseCode());
                System.out.println("Title: " + course.getTitle());
                System.out.println("Professor: " + course.getProfessor());
                System.out.println("Credits: " + course.getCredits());
                System.out.println("Prerequisites: " + course.getPrerequisites());
                System.out.println("Timings: " + course.getTimings());
                System.out.println("Syllabus: " + course.getSyllabus());
                System.out.println();
            }
        }
        if (!printed) {
            System.out.println("No courses available for your semester.");
        }
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getCredits() {
        return credits;
    }

    public void registerForCourse(String courseCode) throws CourseFullException {
        Course course = Course.findCourseByCode(courseCode);
        if (currCourses.contains(course)) {
            System.out.println("Already registered for this course.");
            return;
        }

        boolean prerequisitesMet = true;
        assert course != null;
        if (course.getPrerequisites()!=null) {
            for (String prerequisite : course.getPrerequisites()) {
                boolean prerequisiteFound = prevCourses.contains(Course.findCourseByCode(prerequisite)) || currCourses.contains(Course.findCourseByCode(prerequisite));
                if (!prerequisiteFound) {
                    prerequisitesMet = false;
                    break;
                }
            }
            if (!prerequisitesMet) {
                System.out.println("Prerequisites not met for this course");
                return;
            }
        }

        if (this.currCredits + course.getCredits() > 20) {
            System.out.println("Credit limit exceeded. You cannot register for this course.");
            return;
        }

        if (course.currClass>=course.getClassLimit()){
            throw new CourseFullException("Cannot register, course is full.");
        }

        this.currCourses.add(course);
        this.credits += course.getCredits();
        course.currClass++;
        updateSchedule();
        course.addStudent(this);
        System.out.println("Successfully registered for the course");
    }

    public void submitComplaint(String complaint) {
        Complaint comp = new Complaint(complaint, this);
        complaints.add(comp);
        System.out.println("Your complaint has been filed");
    }

    public void checkComplaints() {
        for (Complaint c : complaints) {
            if (c.getFiledBy().equals(this)) {
                System.out.println(c.getComplaintNumber() + " | " + c.getComplaint());
                if (c.isResolved()) {
                    System.out.println("Status: Resolved");
                }
                else {
                    System.out.println("Status: Pending");
                }
            }
        }
    }

    private void updateSchedule() {
        this.schedule = new ArrayList<>();
        for (Course course: currCourses) {
            HashMap<String, String> timings = course.getTimings();
            boolean dayFound = false;

            for (Map.Entry<String, String> entry : timings.entrySet()) {
                String day = entry.getKey();
                String timing = entry.getValue();

                for (ArrayList<String> daySchedule : schedule) {
                    if (daySchedule.get(0).startsWith(day)) {
                        daySchedule.add("Class: " + course.getTitle() + " - " + timing);
                        dayFound = true;
                        break;
                    }
                }

                if (!dayFound) {
                    ArrayList<String> newDaySchedule = new ArrayList<>();
                    newDaySchedule.add(day + ":");
                    newDaySchedule.add("Class: " + course.getTitle() + " - " + timing);
                    schedule.add(newDaySchedule);
                }

                dayFound = false;
            }
        }
    }

    public void viewSchedule() {
        System.out.println(getSchedule());
    }

    public String getSchedule() {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<String> daySchedule : this.schedule) {
            for (String entry : daySchedule) {
                sb.append(entry).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public ArrayList<Course> getCurrCourses() {
        return currCourses;
    }

    public ArrayList<Course> getPrevCourses() {
        return prevCourses;
    }

    public void addCourseScore(String courseCode, Double score) {
        courseScores.put(courseCode, score);
        System.out.println("Score added for course " + courseCode);
        updateCGPA();
    }

    public void updateCGPA() {
        double totalScore = 0.0;
        int courseCount = 0;

        for (Course course : currCourses) {
            String courseCode = course.getCourseCode();
            if (courseScores.containsKey(courseCode)) {
                totalScore += courseScores.get(courseCode);
                courseCount++;
            }
        }

        if (courseCount > 0) {
            cgpa = (totalScore / courseCount);
        } else {
            cgpa = 0.0;
        }
    }

    public double getCGPA() {
        updateCGPA();
        return cgpa;
    }

    public void updateSGPA() {
        if (this.semester == 1) {
            sgpa = 0.0;
        }
        double total = 0.0;
        int courses = 0;
        for (Course course : prevCourses) {
            String courseCode = course.getCourseCode();
            if (courseScores.containsKey(courseCode)) {
                total += courseScores.get(courseCode);
                courses++;
            }
        }
        sgpa = (total/courses);
    }

    public double getSGPA() {
        updateSGPA();
        return sgpa;
    }

    public void addPrevCourse(String code) {
        Course course = null;
        for (Course c : courseCatalog) {
            if (Objects.equals(c.getCourseCode(), code)){
                course = c;
                break;
            }
        }
        if (course == null) {
            System.out.println("Invalid code");
            return;
        }
        prevCourses.add(course);
    }

    ///////////////////////////////////////////////

}
