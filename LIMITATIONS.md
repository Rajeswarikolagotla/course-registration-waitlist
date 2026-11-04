# Limitations and Follow-up Risks

- **In-memory only:** all course, prerequisite and queue state is lost on restart. Production would need durable, transactional persistence.
- **Single-JVM synchronization:** the `synchronized` lock protects one instance, not distributed writers. The concurrency test checks only threads in one JVM.
- **Simulated eligibility:** prerequisite completion is manually represented; not validated against a registrar. Revocation does not retroactively eject enrolled learners by design.
- **Retained ineligible waiters:** they can remain indefinitely; the promotion scan is O(W) per vacancy.
- **No identity, security, frontend or external API:** intentionally a bounded service/CLI slice, not a deployable registration platform.
- **Review boundary:** AI-assisted code was locally compiled and exercised; independent human review has not been claimed.
- **Separate rubric unavailable:** `Course-Registration-Rubric-Score.md` was referenced in the PDF but not supplied.
