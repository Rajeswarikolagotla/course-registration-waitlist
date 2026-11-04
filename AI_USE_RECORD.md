# AI Use Record — Technical Questions, Suggestions and Decisions

## Record scope and provenance

This document explains how AI assisted the engineering review of the **single-course Java enrollment and FIFO waitlist feature**. The prompt/response entries below are **reconstructed technical consultation summaries** based on the actual requirements, implementation revisions and testing in this project. They are **not a verbatim transcript of previously exchanged prompts**. The original user requests included the assignment's enrollment, prerequisite, FIFO, tests and AI-use requirements, and a later request to follow the candidate expectations strictly.

The questions are written in first person to make the engineering considerations easy to follow. They show **specific decisions and why suggestions were accepted, adjusted or rejected**, without claiming that AI did none of the implementation or that an independent human review has already occurred.

---

### Prompt 1 — Choosing a FIFO data structure

**Technical question (reconstructed):**
> I plan to use Java collections for a single offering. I need FIFO order and no duplicate learner IDs. Should I use a Queue, ArrayList or LinkedHashSet? I want to keep the solution small.

**AI suggestion (summary):**
A `LinkedHashSet<String>` preserves insertion order while providing membership checks and preventing duplicates. A `Queue` would need separate duplicate tracking; an `ArrayList` would require explicit checks.

**Decision:** **Accepted with review.** Use separate `LinkedHashSet` collections for enrolled and waitlisted learners, with cross-collection checks to ensure a learner is in only one location.

**Verification / files:** Duplicate enrollment and duplicate waitlist tests; `CourseService.java`, `CourseServiceTest.java`.

### Prompt 2 — Prerequisite rules at enrollment and promotion

**Technical question (reconstructed):**
> I already check the prerequisite before enrollment. If a learner qualified when joining the waitlist but becomes ineligible before promotion, should I remove them or keep them and move to the next eligible learner?

**AI suggestion (summary):**
Possible policies include removing the ineligible learner or retaining their position while skipping them during promotion. Either can satisfy the assessment if documented and consistently applied.

**Decision:** **Accepted one option, rejected the other.** Reject *initially* ineligible enrollment requests. For a learner who becomes ineligible *after joining*, **retain their place and skip them** until eligible again. Reject automatic removal because it would make a temporary eligibility change permanently discard queue position.

**Verification / files:** Prerequisite failure, revoked prerequisite skip, restored eligibility tests; `CourseService.java`, `DESIGN.md`.

### Prompt 3 — Preventing overbooking under concurrency

**Technical question (reconstructed):**
> With an in-memory Java service, two requests might see the last seat at the same time. Is synchronized enough here, or do I need database transactions or a concurrent collection?

**AI suggestion (summary):**
A shared lock around **check + update + promotion** is required. A concurrent collection by itself does not make the multi-step operation atomic. Java `synchronized` methods are appropriate for one service instance; persistence or transactional storage would be needed for multiple JVMs.

**Decision:** **Accepted `synchronized`, rejected a database for this assignment.** This is a bounded in-memory feature, not a multi-node registration platform.

**Verification / files:** Concurrent enrollment test with 60 learners; asserts capacity is not exceeded and registrations remain unique. `CourseService.java`, `CourseServiceTest.java`.

### Prompt 4 — Reviewing a vacant-seat edge case

**Technical question (reconstructed from the code review):**
> The waitlist may contain only learners whose prerequisites have been revoked. If a seat becomes free, should a new eligible learner be blocked just because the waitlist is not empty?

**AI suggestion (summary):**
First attempt to promote the earliest *eligible* queued learner. If none is eligible and there is capacity, allow a new eligible learner to enroll while retaining the ineligible waitlist entries.

**Decision:** **Accepted after correcting the implementation.** The earlier condition checked `capacity available && waitlist empty`, which could leave seats unused. Changed it to enroll when capacity is available **after attempting eligible promotion**. Retained ineligible entries are not removed.

**Verification / files:** Added the "eligible newcomer fills vacancy when queued learners ineligible" test and an eligible-waiter priority test; `CourseService.java`, `CourseServiceTest.java`.

### Prompt 5 — Avoiding unnecessary application components

**Technical question (reconstructed):**
> The assessment allows a small service or CLI if there is no starter project. Do I need a frontend and HTTP server to demonstrate the feature?

**AI suggestion (summary):**
A browser interface could demonstrate behavior visually, but the assessment explicitly excludes frontend work. A service and CLI demo are sufficient and easier to review.

**Decision:** **Rejected the extra UI/API approach for the final version.** Although an earlier request led to an HTML/JavaScript UI and lightweight Java server, the later scope review prioritized the assessment's explicit **no-frontend** instruction. Retained `CourseService` and a minimal `Demo.java`.

**Verification / files:** Repository scope review confirmed no `web/` files or `Server.java` in the final revision; `Demo.java`, `README.md`.

### Prompt 6 — Focused testing and error behavior

**Technical question (reconstructed):**
> What minimum test cases should I cover so I can validate capacity, FIFO order, duplicates, ineligible learners and repeated drops without introducing a test framework?

**AI suggestion (summary):**
Use a dependency-free Java test runner with assertions for normal enrollment, full-capacity waitlisting, duplicate requests, FIFO promotion, prerequisite failure, revoked/restored eligibility, invalid IDs, repeated drops, cancellation, edge cases and concurrency.

**Decision:** **Accepted and expanded.** Kept the tests focused on invariants rather than adding a database, UI automation or a broad framework. Invalid IDs produce `IllegalArgumentException`; repeat drops produce a clear `NOT_REGISTERED` result without mutation.

**Verification / files:** `CourseServiceTest.java`, `test.sh`, `test-results.txt`.

---

## Verification evidence

The final Java implementation was compiled and exercised locally with OpenJDK 21:

```bash
bash test.sh
bash run.sh
```

Recorded outcome: **14 focused tests passed, 0 failures**, including FIFO promotion, skip/retain behavior, the previously identified vacant-seat issue and concurrent capacity/uniqueness. See `test-results.txt` for the test output and `demo-results.txt` for the CLI demonstration.

## Integrity note

- The quoted **technical questions are reconstructed**, not asserted to be exact earlier messages; AI suggestions are summarized rather than fabricated as verbatim transcripts.
- AI contributed analysis, code suggestions, review and testing support. The record describes selective engineering decisions but does **not** claim a human independently wrote or verified every line.
- Before submitting a personal statement of independent review, the candidate should inspect the Java code, confirm the documented assumptions, and rerun the tests.
- No passwords, tokens, credentials or unnecessary sensitive data are included.
