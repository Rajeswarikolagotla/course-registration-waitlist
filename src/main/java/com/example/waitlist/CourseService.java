package com.example.waitlist;

import java.util.*;

/** One offering, in-memory, synchronized mutations and snapshots. */
public final class CourseService {
    public record Result(String code, String message, String learner, String promoted) {}
    public record Snapshot(int capacity, List<String> enrolled, List<String> waitlisted) {}
    private final int capacity;
    private final Set<String> completedPrerequisites = new HashSet<>();
    private final LinkedHashSet<String> enrolled = new LinkedHashSet<>();
    private final LinkedHashSet<String> waitlist = new LinkedHashSet<>();

    public CourseService(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }
    private String valid(String learner) {
        if (learner == null || !learner.matches("[A-Za-z0-9_-]{1,40}"))
            throw new IllegalArgumentException("Learner ID must be 1-40 letters, numbers, _ or -");
        return learner;
    }
    public synchronized Result setPrerequisite(String learner, boolean completed) {
        learner = valid(learner);
        if (completed) completedPrerequisites.add(learner);
        else completedPrerequisites.remove(learner);
        // Revocation does not eject existing enrollments; promotion checks current eligibility.
        return new Result("UPDATED", "Prerequisite status updated", learner, null);
    }
    public synchronized Result enroll(String learner) {
        learner = valid(learner);
        if (enrolled.contains(learner)) return new Result("ALREADY_ENROLLED", "Already enrolled", learner, null);
        if (waitlist.contains(learner)) return new Result("ALREADY_WAITLISTED", "Already waitlisted", learner, null);
        if (!completedPrerequisites.contains(learner)) return new Result("INELIGIBLE", "Prerequisite not completed", learner, null);
        // No bypass: if a queue exists, eligible queued learners take priority.
        promote();
        if (enrolled.size() < capacity) {
            enrolled.add(learner);
            return new Result("ENROLLED", "Enrollment successful", learner, null);
        }
        waitlist.add(learner);
        return new Result("WAITLISTED", "Course full; joined FIFO waitlist", learner, null);
    }
    public synchronized Result drop(String learner) {
        learner = valid(learner);
        if (enrolled.remove(learner)) {
            String promoted = promote();
            return new Result("DROPPED", promoted == null ? "Dropped" : "Dropped and promoted " + promoted, learner, promoted);
        }
        if (waitlist.remove(learner)) return new Result("WAITLIST_REMOVED", "Removed from waitlist", learner, null);
        return new Result("NOT_REGISTERED", "Learner is neither enrolled nor waitlisted", learner, null);
    }
    /** Ineligible waitlisted learners are retained in their original positions, skipped during promotion. */
    private String promote() {
        String firstPromoted = null;
        while (enrolled.size() < capacity) {
            String eligible = null;
            for (String candidate : waitlist) {
                if (completedPrerequisites.contains(candidate)) { eligible = candidate; break; }
            }
            if (eligible == null) break;
            waitlist.remove(eligible);
            enrolled.add(eligible);
            if (firstPromoted == null) firstPromoted = eligible;
        }
        return firstPromoted;
    }
    public synchronized Snapshot snapshot() {
        return new Snapshot(capacity, List.copyOf(enrolled), List.copyOf(waitlist));
    }
}
