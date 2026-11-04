package com.example.waitlist;

import java.util.*;
import java.util.concurrent.*;

/** Dependency-free automated regression suite; exits nonzero on failure. */
public final class CourseServiceTest {
    private static int passed;
    private static void check(boolean condition, String message) {if (!condition) throw new AssertionError(message);}
    private static void test(String name, Runnable run) {run.run();passed++;System.out.println("PASS " + name);}
    private static void eligible(CourseService s, String... ids) {for(String id:ids)s.setPrerequisite(id,true);}
    public static void main(String[] args) throws Exception {
        test("enroll with capacity",()->{var s=new CourseService(2);eligible(s,"a");check(s.enroll("a").code().equals("ENROLLED"),"enroll");});
        test("full course waitlist",()->{var s=new CourseService(1);eligible(s,"a","b");s.enroll("a");check(s.enroll("b").code().equals("WAITLISTED"),"waitlist");});
        test("duplicate enrolled",()->{var s=new CourseService(1);eligible(s,"a");s.enroll("a");check(s.enroll("a").code().equals("ALREADY_ENROLLED")&&s.snapshot().enrolled().size()==1,"duplicate enrolled");});
        test("duplicate waitlist",()->{var s=new CourseService(1);eligible(s,"a","b");s.enroll("a");s.enroll("b");check(s.enroll("b").code().equals("ALREADY_WAITLISTED")&&s.snapshot().waitlisted().size()==1,"duplicate queue");});
        test("FIFO promotion",()->{var s=new CourseService(1);eligible(s,"a","b","c");s.enroll("a");s.enroll("b");s.enroll("c");check("b".equals(s.drop("a").promoted()),"first");check("c".equals(s.drop("b").promoted()),"second");});
        test("prerequisite failure",()->{var s=new CourseService(1);check(s.enroll("a").code().equals("INELIGIBLE")&&s.snapshot().enrolled().isEmpty(),"prerequisite");});
        test("skip revoked and retain",()->{var s=new CourseService(1);eligible(s,"a","b","c");s.enroll("a");s.enroll("b");s.enroll("c");s.setPrerequisite("b",false);check("c".equals(s.drop("a").promoted()),"skip");check(s.snapshot().waitlisted().equals(List.of("b")),"retained");});
        test("retained learner eventually promoted",()->{var s=new CourseService(1);eligible(s,"a","b","c");s.enroll("a");s.enroll("b");s.enroll("c");s.setPrerequisite("b",false);s.drop("a");s.setPrerequisite("b",true);check("b".equals(s.drop("c").promoted()),"reeligible");});
        test("invalid IDs",()->{var s=new CourseService(1);boolean thrown=false;try{s.enroll("bad id");}catch(IllegalArgumentException e){thrown=true;}check(thrown&&s.snapshot().enrolled().isEmpty(),"invalid");});
        test("repeated drop",()->{var s=new CourseService(1);eligible(s,"a");s.enroll("a");s.drop("a");check(s.drop("a").code().equals("NOT_REGISTERED"),"repeated drop");});
        test("waitlist cancellation",()->{var s=new CourseService(1);eligible(s,"a","b");s.enroll("a");s.enroll("b");check(s.drop("b").code().equals("WAITLIST_REMOVED")&&s.snapshot().waitlisted().isEmpty(),"cancel");});
        test("capacity invariant under concurrency",()->{
            var s=new CourseService(3);int count=60;for(int i=0;i<count;i++)s.setPrerequisite("u"+i,true);
            ExecutorService pool=Executors.newFixedThreadPool(12);
            try {
                List<Future<?>> futures=new ArrayList<>();
                for(int i=0;i<count;i++){final int n=i;futures.add(pool.submit(()->s.enroll("u"+n)));}
                for(Future<?> f:futures)f.get();
                var snap=s.snapshot();
                check(snap.enrolled().size()==3&&snap.waitlisted().size()==57,"capacity");
                Set<String> unique=new HashSet<>(snap.enrolled());unique.addAll(snap.waitlisted());check(unique.size()==60,"unique");
            } catch(Exception e){throw new RuntimeException(e);}finally{pool.shutdownNow();}
        });
        System.out.println("ALL TESTS PASSED: "+passed);
    }
}
