# Design Specification — Single Course Offering

## Boundary, model and APIs
No starter project or prescribed data model was supplied. The fallback is a single in-memory `CourseService`. Its state is an integer capacity, `LinkedHashSet<String>` enrolled IDs, `LinkedHashSet<String>` FIFO waitlist IDs, and `HashSet<String>` of learners who have completed one prerequisite. No database, network API, frontend, catalog or identity system.

Public Java APIs: `CourseService(capacity)` (capacity >= 1), `setPrerequisite(id, boolean)`, `enroll(id)`, `drop(id)`, and `snapshot()`. Enrollment returns `ENROLLED`, `WAITLISTED`, `INELIGIBLE`, `ALREADY_ENROLLED`, or `ALREADY_WAITLISTED`. Drop returns `DROPPED` (with optional promoted ID), `WAITLIST_REMOVED`, or `NOT_REGISTERED`. Null or invalid IDs (not matching `[A-Za-z0-9_-]{1,40}`) throw `IllegalArgumentException` before mutation.

## Prerequisite and FIFO waitlist policy
1. A learner is eligible only if the simulated prerequisite is marked complete. An **initially ineligible** learner is rejected rather than waitlisted.
2. A learner may enroll only once or join the queue once. Duplicate requests return clear no-op codes.
3. If a learner becomes ineligible **after joining the queue**, they **remain at their original position but are skipped** for promotion. The earliest **currently eligible** queued learner is promoted on vacancy. If eligibility is restored, the retained learner becomes eligible from their original FIFO position.
4. Revoking eligibility of an already enrolled learner does not automatically eject that learner. New enrollment/promotion checks current eligibility.
5. An eligible waiting learner has priority over a new arrival. If all waiting learners are currently ineligible, an eligible new arrival may fill an available seat without removing the retained ineligible entries.
6. Dropping an enrolled learner opens a seat and triggers promotion; dropping from the waitlist cancels the entry. Repeated/unknown drops are no-ops.

## Invariants
- `0 <= enrolled.size() <= capacity`.
- Enrolled IDs and waitlist IDs are unique and disjoint.
- Duplicate requests neither change capacity nor reorder the queue.
- Each *new* enrollment or promotion requires current eligibility, although existing enrolled learners may later lose eligibility.
- Vacant seats go to the earliest currently eligible waiter, when one exists.

## Algorithm, concurrency and tradeoffs
`enroll` validates eligibility, promotes eligible waiting learners into existing vacancies, then enrolls the newcomer if a seat remains or appends them to the FIFO queue. `drop` removes the learner, scans the waitlist in insertion order, and promotes the first eligible learner; ineligible learners are retained. Promotion scanning costs O(W) per seat.

All public service operations and snapshots are `synchronized` on the same object. Check/mutate/promote is atomic in one JVM. The suite includes a 60-request concurrent enrollment test verifying capacity and uniqueness. State is in memory and disappears on restart; cross-process transactions and production identity/security are intentionally out of scope. `Demo.java` is a tiny executable illustration rather than a new application platform.
