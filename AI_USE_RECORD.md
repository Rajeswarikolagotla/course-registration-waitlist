# AI Use Record — Targeted Engineering Consultation

## Purpose and ownership

This assignment uses a small, in-memory Java service for a single course offering. The engineering decisions in this record focus on prerequisite validation, seat capacity, FIFO promotion, duplicate prevention and safe mutation under concurrency.

AI was consulted to explore implementation options, identify edge cases and suggest focused test scenarios. Suggestions were **reviewed and selectively accepted, adjusted or rejected** against the assessment's acceptance criteria. This log does **not** claim that the implementation was completed without AI assistance or that a separate human reviewer has independently certified it.

## Recorded user requests (actual excerpts)

The user provided these instructions in the project conversation:

1. "Implement enrollment, dropping, and FIFO waitlist promotion for one course offering ... A learner may enroll only once."
2. "State the prerequisite and waitlist assumptions. In particular, define whether an ineligible learner is skipped or remains waiting, and apply that policy consistently."
3. "Tests cover available capacity, full capacity, duplicate requests, FIFO promotion, prerequisite failure, and invalid or repeated drops."
4. "please check the deliverables and candidate expectations and act according"

The technical questions in the next section are **concise summaries of the review topics**, not quotations of additional historical prompts. They should not be represented as separate verbatim messages that were sent.

## Technical questions, suggestions and decisions

| Technical question examined | AI suggestion / response summary | Decision and engineering rationale | Related files |
|---|---|---|---|
| How should the queue preserve FIFO order and prevent duplicates without an extra policy engine? | Use `LinkedHashSet<String>` for waitlisting and `Set` semantics for enrollment, preserving insertion order while rejecting repeated IDs. | **Accepted with review:** the service uses separate enrolled and waiting sets and checks both before mutation. This keeps the model small and enforces uniqueness. | `CourseService.java`, `DESIGN.md` |
| Should an initially ineligible learner join the queue? What happens if an already-waitlisted learner loses eligibility? | Reject initially ineligible requests; retain but skip an existing waiter whose prerequisite becomes invalid, and promote the earliest currently eligible waiter. | **Accepted and explicitly documented:** this policy is consistent with the stated prerequisite and queue requirements. Eligibility restoration keeps the learner's original queue position. | `CourseService.java`, `DESIGN.md`, `CourseServiceTest.java` |
| How should concurrent requests avoid exceeding course capacity? | Protect check-enroll-drop-promote operations using one lock around the in-memory service. | **Accepted for this scope:** synchronized methods protect the one-JVM store. A database/distributed lock was **rejected** as unnecessary for the supplied minimal-service fallback. | `CourseService.java`, concurrency test |
| Can an ineligible retained waitlist entry block an otherwise vacant seat? | The original conditional required both free capacity and an empty waitlist, which could unnecessarily block an eligible newcomer when every waiter was ineligible. | **Rejected and corrected:** after trying promotion, enrollment now depends on available capacity, not an empty waitlist. The ineligible queued entries remain intact. Added a targeted regression test. | `CourseService.java`, `CourseServiceTest.java` |
| Is a UI or full HTTP application necessary? | A Java HTTP server and HTML/JavaScript interface were initially suggested and implemented in response to a request for them. | **Rejected for the final assessment:** the later explicit candidate expectations say *do not build a frontend*. Removed the HTTP/UI files and retained a Java service with a tiny CLI demo. | `Demo.java`, `README.md` |
| Which tests provide the strongest evidence without overengineering? | Test enrollment with seats, full-capacity waitlisting, duplicates, FIFO order, prerequisite failure, revoked/restore eligibility, invalid and repeated drops, plus concurrency. | **Accepted and expanded:** added eligible-newcomer vacancy and queue-priority cases for 14 focused tests. Avoided a separate framework or large integration suite. | `CourseServiceTest.java`, `test-results.txt` |

## Verification of AI-assisted changes

The final Java code was compiled and the focused tests were executed locally with OpenJDK 21 using:

```bash
bash test.sh
bash run.sh
```

Recorded results: **14 tests passed, 0 failures**, including concurrent enrollment uniqueness/capacity and the corrected retained-ineligible-waiter vacancy case. The command output is preserved in `test-results.txt` and the CLI example in `demo-results.txt`.

Verification review focused on these invariants:

- Enrollment count never exceeds capacity.
- A learner cannot appear twice or be simultaneously enrolled and waitlisted.
- The earliest **eligible** queued learner is promoted on a vacancy.
- Ineligible retained learners are skipped without losing their FIFO position.
- Invalid and repeated requests do not corrupt state.

## Boundaries and integrity of the record

This document records the actual user requests above and summarizes the technical review topics arising during the AI-assisted implementation. It does not invent a verbatim technical Q&A transcript or claim independent human validation. The person submitting the assessment should inspect the implementation and execute the tests personally before signing off on a claim of independent review.

No credentials, secrets, tokens, or sensitive personal data are included in this AI use record.
