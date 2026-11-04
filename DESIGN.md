# Design specification

## Boundary and model

Single in-memory course offering. `CourseService` owns positive `capacity`, an ordered set of `enrolled` learner IDs, an ordered set of `waitlist` learner IDs, and a set of learners who have completed the prerequisite. `Server` adapts form-encoded HTTP requests to service calls and serves static assets. The browser never owns authoritative enrollment state.

## Contract

- `setPrerequisite(id, completed)` updates simulated eligibility for the demo.
- `enroll(id)` returns `ENROLLED`, `WAITLISTED`, `ALREADY_ENROLLED`, `ALREADY_WAITLISTED`, or `INELIGIBLE`.
- `drop(id)` returns `DROPPED` (and optional `promoted`), `WAITLIST_REMOVED`, or `NOT_REGISTERED`.
- `snapshot()` returns an immutable state snapshot.
- Invalid IDs throw `IllegalArgumentException`, mapped to HTTP 400. IDs must match `[A-Za-z0-9_-]{1,40}`.

## Assumptions and policy

1. One prerequisite exists and is considered satisfied only for learner IDs explicitly marked complete. No external records or identity system exist.
2. Learners must be eligible **when initially requesting enrollment**. Ineligible requests are rejected rather than queued.
3. If a waitlisted learner later loses eligibility, they **remain in their original queue position but are skipped** during promotion. The first currently eligible learner is promoted. If eligibility is restored before a later vacancy, they are eligible again from their retained position.
4. Prerequisite revocation does not retroactively remove an enrolled learner. It prevents future promotion after a drop.
5. Dropping a waitlisted learner cancels their request; dropping an unknown learner returns a clear no-op result.
6. A newly arriving learner cannot bypass existing eligible queued learners; the service attempts promotion before direct enrollment.

## Invariants

- `0 <= enrolled.size() <= capacity`.
- Each ID appears at most once in each collection and cannot appear in both.
- Only eligible learners are newly enrolled or promoted (an existing enrolled learner may later lose eligibility).
- Waitlist insertion order is stable unless a learner is removed or promoted.
- Each vacancy promotes the earliest currently eligible waiting learner, if one exists.

## Algorithm and concurrency

All service mutations and snapshots are `synchronized` on the same service instance. LinkedHashSet gives deterministic FIFO insertion order and duplicate prevention. On drop, remove the learner and scan the queue for the first eligible ID; skip but retain ineligible IDs; remove and enroll the selected learner atomically. Promotion repeats until no vacancy or no eligible waiting learner remains. Complexity is O(W) per promoted seat, acceptable for this bounded prototype.

## Tradeoffs

JDK built-in `HttpServer` avoids external dependencies. UI is intentionally thin and optional relative to assessment scope. State is lost on restart. The prerequisite endpoint is a demo/test control, not an authenticated academic record. No persistence, distributed locks, auditing, user identity, or production security are provided.
