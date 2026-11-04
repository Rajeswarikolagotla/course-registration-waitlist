# AI Prompt and Use Record

## Context
The user supplied the Course Waitlist assessment requirements, with no starter project or separate rubric file. The original assignment permits a minimal in-memory service/CLI. This record is a candid description of AI-assisted implementation and executable verification, **not a claim of independent human review**. No credentials, tokens, or sensitive learner data appear in the recorded prompts.

| Prompt used / relevant excerpt | AI output or summary | Accepted, edited or rejected | File/decision |
|---|---|---|---|
| "Implement enrollment, dropping, and FIFO waitlist promotion ... State the prerequisite and waitlist assumptions" | One synchronized Java service with a deterministic ordered queue, duplicate prevention, and prerequisite checks. | **Accepted/edited.** Selected `LinkedHashSet`, explicit ineligible-learner skip-but-retain policy and result codes. | `CourseService.java`, `DESIGN.md` |
| "Tests cover available capacity, full capacity, duplicate requests, FIFO promotion, prerequisite failure, and invalid or repeated drops ... concurrency test" | Build focused service tests including a concurrency stress case. | **Accepted/expanded.** Added revoked eligibility, restored eligibility, cancellation, vacant-seat edge case, waiting priority and concurrency assertions. | `CourseServiceTest.java`, `test-results.txt` |
| "do the above assignment using java backend and javascript frontend html" | A Java HTTP server and browser UI were initially implemented. | **Rejected for final assessment scope.** The later explicit request prioritized the PDF's no-frontend candidate expectations; removed both, retaining the Java service/CLI fallback. | Removed `Server.java` and `web/`; added `Demo.java` |
| "please check the deliverables and candidate expectations and act according" | Recheck queue/capacity invariants and test code rather than relying on initial generation. | **Accepted/edited.** Discovered that an ineligible retained queue member could block an eligible new learner from a vacant seat; modified capacity check and added regression coverage. | `CourseService.java`, `CourseServiceTest.java`, `DESIGN.md` |

## Verification evidence and review boundary
Test and demo were executed locally with OpenJDK 21:
```bash
bash test.sh
bash run.sh
```
The attached `test-results.txt` records **14/14 passed** including the concurrent capacity/uniqueness test; `demo-results.txt` captures the CLI example. The corner case above was found by reviewing the service logic against the capacity acceptance criterion and was fixed before executing the final regression suite.

The review and verification were AI-assisted with actual compiler/test execution. **No independent human reviewer has reviewed or approved the code**; a submitting candidate should read the implementation and rerun the tests before personally attesting to independent review. The separate `Course-Registration-Rubric-Score.md` was not provided and is not invented.
