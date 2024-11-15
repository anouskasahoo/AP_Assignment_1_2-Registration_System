package com.iiitd.registration;

import java.util.Scanner;

import static com.iiitd.registration.Main.feedbacks;

public class Feedback<T> {
    private Course course;
    private T feedback;
    private String desc;

    public Feedback(Course course, T feedback, String desc) {
        this.course=course;
        this.feedback = feedback;
        this.desc = desc;
        feedbacks.add(this);
    }

    private static Scanner scanner = new Scanner(System.in);

    public Course getCourse() {
        return course;
    }

    public T getFeedback() {
        return feedback;
    }

    public String getDesc() {
        return desc;
    }


    public static void addFeedback(Student st) {
        System.out.println("Enter course code to give feedback about: ");
        String feedcode = scanner.nextLine();
        Course c=Course.findCourseByCode(feedcode);
        if (c==null){
            System.out.println("Invalid course code");
            return;
        }
        if (!st.getCurrCourses().contains(c)&&!st.getPrevCourses().contains(c)){
            System.out.println("You are not a current or previous student of this course");
            return;
        }
        System.out.println("Select your choice of feedback-");
        System.out.println("1. Numeric feedback");
        System.out.println("2. Textual feedback");

        int feedpick = scanner.nextInt();
        scanner.nextLine();

        switch (feedpick){
            case 1:
                System.out.println("Enter your feedback [Range - 0 to 5; 0 - Poor; 5 - Excellent]- ");
                int intfeed = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Enter 1 if you want to add a description; Enter 2 otherwise ");
                int intdescyn = scanner.nextInt();
                scanner.nextLine();

                String intdesc = null;
                if (intdescyn==1){
                    System.out.println("Enter the description- ");
                    intdesc = scanner.nextLine();
                }

                new Feedback<Integer>(c,intfeed,intdesc);
                break;
            case 2:
                System.out.println("Enter your feedback- ");
                String strfeed = scanner.nextLine();

                System.out.println("Enter 1 if you want to add a description; Enter 2 otherwise ");
                int strdescyn = scanner.nextInt();
                scanner.nextLine();

                String strdesc = null;
                if (strdescyn==1){
                    System.out.println("Enter the description- ");
                    strdesc = scanner.nextLine();
                }
                new Feedback<String>(c,strfeed,strdesc);
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }

        System.out.println("Your feedback has been noted");
    }

}
