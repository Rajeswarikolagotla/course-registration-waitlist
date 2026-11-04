# AI Assistance Record — Technical Q&A and Review Decisions

This record is a **retrospective summary of actual prompts and decisions** from the course-registration assessment conversation. AI suggestions below are summarized, not presented as verbatim historical responses. It does not claim a separate, unaided human code review.

## 1. How should the enrollment and FIFO waitlist work?

**User prompt (excerpt):** “Implement enrollment, dropping, and FIFO waitlist promotion for one course offering ... A learner may enroll only once. When the course is full, eligible learners join the waitlist.”

**AI suggestion (summary):** Build one Java service with an in-memory enrollment set and an ordered waitlist. Return explicit outcomes for enrollment, waitlisting and duplicates. When a seat opens, scan from the front and promote the earliest eligible learner.

**Decision — Accepted with edits:** Used `LinkedHashSet` for uniqueness plus insertion order, explicit result codes, and a synchronized promotion method. Added a documented rule for ineligible waitlisted learners rather than silently removing them.

**Affected files:** `src/main/java/com/example/waitlist/CourseService.java`, `DESIGN.md`.

## 2. How should prerequisite failures and ineligible waiting learners be handled?

**User prompt (excerpt):** “State the prerequisite and waitlist assumptions. In particular, define whether an ineligible learner is skipped or remains waiting, and apply that policy consistently.”

**AI suggestion (summary):** Reject learners who fail the prerequisite at the time of the initial request; retain learners who later lose eligibility on the waitlist, skipping them during promotion until they become eligible again.

**Decision — Accepted and clarified:** Initial ineligible requests are rejected. Later-ineligible waitlisted learners keep their original positions but are skipped for promotion. Existing enrollments are not revoked automatically. During review, a vacant-seat edge case was identified: retained ineligible waiters must not prevent an eligible new learner from filling an otherwise empty seat. The enrollment check was corrected and a regression test added.

**Affected files:** `CourseService.java`, `CourseServiceTest.java`, `DESIGN.md`.

## 3. What tests are necessary to establish reliability?

**User prompt (excerpt):** “Tests cover available capacity, full capacity, duplicate requests, FIFO promotion, prerequisite failure, and invalid or repeated drops. If the starter uses concurrent requests, preserve its transaction or locking pattern and add a focused concurrency test where practical.”

**AI suggestion (summary):** Use a dependency-free Java test suite with assertions for acceptance criteria and a multi-threaded enrollment test to detect overbooking or duplicates.

**Decision — Accepted and expanded:** Added tests for prerequisite revocation, re-eligibility, waitlist cancellation, vacant-seat behavior, waiting priority, and concurrent enrollment. Mutations and snapshots use the same instance-level `synchronized` lock.

**Affected files:** `src/test/java/com/example/waitlist/CourseServiceTest.java`, `test.sh`, `test-results.txt`.

**Verification:** The documented local execution of `bash test.sh` reports 14 passing tests. Reviewers can rerun the command against the submitted source.

## 4. Should this solution include a Java HTTP server and HTML/JavaScript frontend?

**User prompt:** “do the above assignment using java backend and javascript frontend html”

**AI suggestion (summary):** A Java HTTP server and small HTML/CSS/JavaScript interface were initially added so enrollment and waitlisting could be demonstrated in a browser.

**Decision — Rejected for the final submission:** In a later request the user explicitly directed the work to follow the assessment’s *Candidate Expectations*, which say that, without a supplied starter project, a small service/CLI fallback should be used and no frontend should be built. The HTTP server and frontend were therefore removed. A minimal `Demo.java` illustrates the service.

**Affected files:** Removed `Server.java` and `web/`; retained `CourseService.java`; added `Demo.java`, `run.sh`, and `demo-results.txt`.

## Verification and limitations

- **Code verification:** Java service/test compilation and execution using the commands in `README.md`. `test-results.txt` and `demo-results.txt` contain the recorded results.
- **Review scope:** The AI-assisted review checked capacity, queue ordering, eligibility and duplicate handling, and corrected the empty-seat edge case. This is **not** evidence of independent human review; the submitting candidate should personally review and run the tests before attesting to that expectation.
- **Data safety:** No credentials, tokens, student records or unnecessary sensitive data were used in the prompts recorded here.
- **Project constraints:** No starter project or separate rubric scorecard was supplied. The project uses the assignment's in-memory CLI fallback.
