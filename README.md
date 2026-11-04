# Course Registration — Java Backend + HTML/JavaScript Frontend

Implements the assessment's one-offering enrollment, drop and FIFO waitlist feature. The original PDF excludes a frontend; this submission **intentionally adds a minimal frontend at the user's explicit request**. No database, framework, authentication or external dependencies.

## Requirements
JDK 21+ (Java 17+ should also work), a browser, and Bash for the convenience scripts. Run from the project root.

## Run
```bash
./run.sh
```
Open http://127.0.0.1:8080. Default course capacity is 2 (set in `Server.java`). Change port with `./run.sh 8090`.

Windows PowerShell:
```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src/main/java/com/example/waitlist/*.java
java -cp out com.example.waitlist.Server
```

## Tests
```bash
./test.sh
```
Windows PowerShell:
```powershell
javac -d out src/main/java/com/example/waitlist/*.java src/test/java/com/example/waitlist/*.java
java -cp out com.example.waitlist.CourseServiceTest
```

## Demo workflow
1. Mark prerequisite complete for `alice`, `bob`, and `carol`.
2. Enroll `alice` and `bob`; both fill the two seats.
3. Enroll `carol`; she is waitlisted.
4. Drop `alice`; `carol` is automatically promoted.
5. Repeat requests to see idempotent/clear result codes.

## API
All POST endpoints accept `application/x-www-form-urlencoded` parameters:
- `POST /api/prerequisite` — `learner=alice&completed=true` or `false`.
- `POST /api/enroll` — `learner=alice`.
- `POST /api/drop` — `learner=alice`.
- `GET /api/state` — capacity, enrolled and waitlisted arrays.

Example:
```bash
curl -X POST -d 'learner=alice&completed=true' http://127.0.0.1:8080/api/prerequisite
curl -X POST -d 'learner=alice' http://127.0.0.1:8080/api/enroll
curl http://127.0.0.1:8080/api/state
```

## Files
- `src/main/java/com/example/waitlist/CourseService.java` — synchronized business logic.
- `src/main/java/com/example/waitlist/Server.java` — lightweight HTTP server and static asset serving.
- `src/test/java/com/example/waitlist/CourseServiceTest.java` — 12 automated test cases.
- `web/` — HTML, CSS, and vanilla JavaScript UI.
- `DESIGN.md`, `AI_USE_RECORD.md`, `LIMITATIONS.md`, `test-results.txt`, `http-smoke-test.txt` — assessment evidence.
