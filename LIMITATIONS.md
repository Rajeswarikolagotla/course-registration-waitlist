# Limitations and follow-up risks

- **No persistence:** restarting the server resets course, queue and prerequisite data.
- **Single JVM:** `synchronized` protects threads in this process only; multiple server processes would require shared transactional persistence.
- **No authentication/authorization:** the prerequisite endpoint is a test/demo control and must not be exposed as-is in production.
- **No anti-CSRF or rate limiting:** HTTP endpoints are intentionally local-only and not production hardened.
- **No external prerequisite records:** prerequisite eligibility is manually simulated.
- **Minimal frontend:** UI added at the user's request despite the original PDF's scope exclusion; core service is independently testable.
- **No provided rubric:** separate rubric named in PDF was unavailable.
- **Test scope:** service suite plus HTTP smoke test, not browser automation or security testing.
