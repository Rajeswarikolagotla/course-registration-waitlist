# Course Waitlist — AI-Assisted Engineering Assessment

A **single Java in-memory service** implementing one offering's enrollment, dropping, prerequisite validation and FIFO promotion. No starter project was supplied; this follows the assessment's permitted minimal service/CLI fallback. **No frontend, database, framework or deployment** is included.

## Requirements and setup
JDK 21 (tested with OpenJDK 21), no external libraries. Run from repository root.

**Run CLI demonstration (Linux/macOS Bash):**
```bash
bash run.sh
```
**Run tests (Linux/macOS Bash):**
```bash
bash test.sh
```
**Windows PowerShell:**
```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src/main/java/com/example/waitlist/CourseService.java Demo.java
java -cp out Demo
javac -d out src/main/java/com/example/waitlist/CourseService.java src/test/java/com/example/waitlist/CourseServiceTest.java
java -cp out com.example.waitlist.CourseServiceTest
```

The demo registers Alice, queues Bob and Carol, drops Alice and promotes Bob. The **14 focused tests** check vacancies, capacity, FIFO, eligibility, duplicates, invalid IDs, repeat drops and concurrent requests. Executed output is in `test-results.txt`.

## Java service API
```java
CourseService course = new CourseService(2);
course.setPrerequisite("alice", true);
CourseService.Result result = course.enroll("alice");
CourseService.Result dropped = course.drop("alice");
CourseService.Snapshot state = course.snapshot();
```
Enrollment codes: `ENROLLED`, `WAITLISTED`, `INELIGIBLE`, `ALREADY_ENROLLED`, `ALREADY_WAITLISTED`. Drop codes: `DROPPED`, `WAITLIST_REMOVED`, `NOT_REGISTERED`. Invalid learner IDs raise `IllegalArgumentException` without state changes. IDs must match `[A-Za-z0-9_-]{1,40}`.

## Files
- `src/main/java/com/example/waitlist/CourseService.java` — single-offering service.
- `src/test/java/com/example/waitlist/CourseServiceTest.java` — 14 assertions-based test cases.
- `Demo.java`, `run.sh`, `test.sh` — runnable example and test commands.
- `DESIGN.md` — boundary, data/API model, invariants, skip/retain policy and synchronization.
- `AI_USE_RECORD.md` — prompts, contribution decisions and verification evidence.
- `LIMITATIONS.md` — risks within scope.
- `test-results.txt`, `demo-results.txt` — actual execution records.

The PDF references `Course-Registration-Rubric-Score.md`, but that separate file was not supplied; scoring was not fabricated.
