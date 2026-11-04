# AI prompt and use record

## Context

User supplied `Course-Registration-AI-Assisted-Assessment.pdf` and requested a Java backend with a JavaScript/HTML frontend. No Java starter project or separate rubric file was supplied. This record documents AI-assisted implementation in this conversation, not a claim of independent human review.

## Prompt 1 — requirements and technology change

**User request:** “please complete the assignment and provide the solution as per the requirement present in the above attached file”; follow-up: “do the above assignment using java backend and javascript frontend html”.

**AI summary:** Proposed a one-offering, in-memory Java service; FIFO waitlist; prerequisite eligibility; synchronized operations; small JDK HTTP adapter and HTML/CSS/JavaScript UI; automated regression tests.

**Disposition:** Accepted the core service and tests. **Edited the original assessment's no-frontend boundary** because the user's follow-up explicitly requested a frontend. Rejected a database, Spring Boot dependency, authentication, and a multi-course catalog as unnecessary to the bounded task.

**Files informed:** `CourseService.java`, `Server.java`, `web/*`, `DESIGN.md`, `README.md`.

## Prompt 2 — independent verification checklist

**AI-generated internal engineering checklist:** Verify capacity, duplicate enrollment and waitlisting, FIFO promotion, prerequisite rejection, revoked prerequisite skipping/retention, invalid IDs, repeated drops, waitlist cancellation, and concurrent enrollment.

**Disposition:** Accepted and translated into explicit executable assertions. Added an eligibility-restoration case and a concurrent uniqueness check to cover edge cases.

**Files informed:** `CourseServiceTest.java`.

## Verification evidence

`test-results.txt` contains actual JDK compilation/test execution output, not simulated output. The 12-case suite executes with `./test.sh`. A local HTTP smoke test checks prerequisite update, enrollment, waitlisting, promotion and JSON state. Evidence is included in `http-smoke-test.txt`.

## Review limitations

The implementation and review were AI-assisted; a human reviewer should inspect the policy assumptions, code, and evidence before claiming independent human verification. The external `Course-Registration-Rubric-Score.md` mentioned in the PDF was not provided, so no rubric score is fabricated.
