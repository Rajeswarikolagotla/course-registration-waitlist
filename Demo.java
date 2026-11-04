import com.example.waitlist.CourseService;

/** Minimal command-line demonstration; core business logic stays in CourseService. */
public final class Demo {
    public static void main(String[] args) {
        CourseService course = new CourseService(1);
        for (String id : new String[]{"alice", "bob", "carol"}) course.setPrerequisite(id, true);
        System.out.println(course.enroll("alice"));
        System.out.println(course.enroll("bob"));
        System.out.println(course.enroll("carol"));
        System.out.println(course.drop("alice"));
        System.out.println(course.snapshot());
    }
}
