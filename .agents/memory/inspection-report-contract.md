---
name: Inspection report contract
description: The authoritative Partner API behavior for booking inspection photos, checklist evidence, and reported property problems.
---

The public booking inspection endpoint returns the normal API envelope with
`data` containing a signed report URL. Opening that URL displays before photos,
after photos, checklist items and their photo evidence, and property problems
reported by the cleaner. There is no separate public inspection-details
endpoint and no public Partner API operation for submitting a problem.

**Why:** The backend Partner API implementation generates a signed report link,
while the report's underlying payload contains `before_photos`, `after_photos`,
`checklist`, and `property_problems`. Earlier SDK documentation incorrectly
treated inspection as inline data and exposed a nonexistent details route.

**How to apply:** Type inspection response data as a URL string. Compatibility
helpers named “details” must delegate to the supported inspection endpoint.
Expose checklist item photo URLs, but do not invent a public problem-submission
operation.