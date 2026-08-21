# Day 5–6 — Service-to-Service Communication, Discovery & Gateway

## Why this is a separate project from `one-enterprise-platform`

Days 2–4 all said "continue using your existing services." Day 5 doesn't —
it walks through building the User Service → Order Service call **from
scratch**, using a different HTTP client (Spring's newer `RestClient`
instead of `RestTemplate`), plain endpoint paths (`/users/{id}`, not
`/api/users/{id}`), and record-based DTOs. That's a deliberate fresh
teaching exercise, not another increment to the bigger platform — so it
lives in its own folder rather than being bolted onto Days 1–4's project.
Day 6 explicitly says to "take the Day 5 Order + User example and upgrade
it," so it extends this project rather than starting yet another one.

If you're looking for the accumulated platform (gateway, resilience
patterns, payment service, etc.), that's the separate
`one-enterprise-platform` project from Days 1–4. Note that project's
gateway (Day 3) routes to fixed `http://localhost:808x` addresses; this
project's gateway (Day 6) routes to Eureka-registered service *names*
instead — that's the actual difference Day 6 is teaching.

```
day5-service-communication/
├── discovery-server/  → Eureka registry, port 8761
├── api-gateway/         → single entry point, routes by service name, port 8080
├── user-service/          → provider API, registered as USER-SERVICE, port 8081
└── order-service/           → consumer, registered as ORDER-SERVICE, port 8082
```

## Prerequisites

- Java 17+
- Maven 3.8+

## Running it

**Start order matters** — the registry first, then the services that
register with it, then the gateway that depends on discovering them.

**Terminal 1 — Discovery Server (port 8761)**
```bash
cd discovery-server
mvn spring-boot:run
```
Wait for it to finish starting, then open http://localhost:8761 — you
should see the Eureka dashboard with no instances registered yet.

**Terminal 2 — User Service (port 8081)**
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 3 — Order Service (port 8082)**
```bash
cd order-service
mvn spring-boot:run
```
Refresh the dashboard — `USER-SERVICE` and `ORDER-SERVICE` should now both
appear under "Instances currently registered with Eureka." If they don't
show up within ~30s, check that each service's console log shows a
successful registration (not a connection-refused retry loop) and that
`discovery-server` is really up first.

**Terminal 4 — API Gateway (port 8080)**
```bash
cd api-gateway
mvn spring-boot:run
```


## Step 3/4 — the successful path (Day 5, direct calls — still works)

```bash
curl http://localhost:8081/users/1001
# {"id":1001,"name":"John","email":"john@example.com"}

curl http://localhost:8082/orders/5001
# {"orderId":5001,"userId":1001,"userName":"John"}
```

The second call only succeeds because Order Service made a real HTTP
request to User Service and used the response — check User Service's
console log for the incoming request if you want to see it happen.
This still calls the fixed `user.service.base-url` from Day 5 — Day 6
doesn't change this internal call (see the Day 6 section below for why).

## Step 5 / Hands-on Challenge 2 — break it on purpose (Day 5 version)

```bash
# Stop User Service (Ctrl+C in Terminal 1), then:
curl -i http://localhost:8082/orders/5001
```

You'll get:
```json
{
  "error": "USER_SERVICE_UNAVAILABLE",
  "message": "User service is unavailable. Please try again later.",
  "status": 503,
  "timestamp": "..."
}
```

That response comes from `GlobalExceptionHandler`, not a try/catch in
`OrderController` — the handbook explicitly prefers centralized handling
over scattering try/catch blocks through controllers, so that's what this
does.

Restart User Service and confirm the normal response comes back:
```bash
curl http://localhost:8082/orders/5001
# back to {"orderId":5001,"userId":1001,"userName":"John"}
```

---

## Day 6 — Discovery Server + API Gateway

Everything above still works exactly as before — Day 6 doesn't change
User Service, Order Service's controller, or `UserClient`. What's new is
two more processes: `discovery-server` (the registry) and `api-gateway`
(the single client entry point), plus a small config change so
User Service and Order Service *register themselves*.

### Hands-on Challenge 3 — call both services through the gateway

With all four processes running (see "Running it" above), the client can
now use one address instead of two:

```bash
curl http://localhost:8080/users/1001
# {"id":1001,"name":"John","email":"john@example.com"}
# same response as calling User Service directly on 8081

curl http://localhost:8080/orders/5001
# {"orderId":5001,"userId":1001,"userName":"John"}
# same response as calling Order Service directly on 8082
```

The routing table lives entirely in
`api-gateway/src/main/resources/application.yml` — `lb://USER-SERVICE` and
`lb://ORDER-SERVICE` target the **registered service names**, not a fixed
host/port. The gateway asks the discovery server "where is USER-SERVICE
right now?" on each request instead of having `localhost:8081` written
down anywhere.

### Deliberately break something (Section 17)

Stop User Service (Ctrl+C in its terminal) while the discovery server and
gateway keep running:

```bash
curl -i http://localhost:8080/users/1001
```

You'll get an error (a gateway-level failure, since there's no
`USER_SERVICE_UNAVAILABLE` translation here the way Order Service has —
the gateway is plain routing, not a place for business-level error
contracts, per Section 20's "don't put business logic in the gateway").
Refresh the Eureka dashboard — after a short delay, `USER-SERVICE` will
drop out of the registered instances list (Eureka needs a few missed
heartbeats to notice, so this isn't instant).

Restart User Service, wait for it to re-register (check the dashboard),
and confirm the gateway call works again — no gateway restart needed.

**Worth reasoning through** (Section 17's own questions): the discovery
server knew User Service *used to be* available and eventually noticed it
wasn't; the gateway performed the routing; the client was only ever
talking to the gateway, and never knew User Service's address changed,
went away, or came back.

### Scope note: Order Service's own call to User Service is unchanged

Order Service now *registers* as `ORDER-SERVICE`, but `UserClient` still
calls the fixed `user.service.base-url` from Day 5 (`http://localhost:8081`),
not a discovered `USER-SERVICE` address. This is a deliberate scope
decision, not an oversight:

- The handbook's own concrete, given code for discovery-based routing
  (`lb://USER-SERVICE`) is Gateway-specific (Spring Cloud Gateway's route
  URIs). It doesn't give equivalent concrete code for a plain
  `RestClient` to resolve a logical service name, and explicitly hedges:
  *"The exact client configuration depends on the Spring Boot / Spring
  Cloud version used by your project."*
- The Day 6 checklist and Mini Project's required list (Section 21) only
  ask for: discovery server running, both services registered, gateway
  routing to both, and the **client** (external caller) using port 8080
  instead of calling services directly. Order Service's internal call to
  User Service isn't in that required list.

If you want to take it further yourself: Spring Cloud Commons supports a
`@LoadBalanced RestClient.Builder` (or `RestTemplate`) that resolves a
logical name like `http://USER-SERVICE` through the same discovery
mechanism the gateway uses — but exactly which annotation/API applies
depends on your Spring Cloud version, per the handbook's own caveat above.
Worth trying as a stretch exercise; not included here to avoid shipping
version-fragile code as if it were verified.

## Automated tests

`OrderControllerTest` covers both paths from the "Mini Project" checklist
without needing two live processes during a build — `UserClient` is
mocked so each test controls exactly what "User Service" does:

```bash
cd order-service
mvn test
```

- `returnsOrderWithUserDetails_whenUserServiceRespondsSuccessfully` — the
  successful end-to-end path
- `returns503_whenUserServiceIsUnavailable` — User Service unreachable

(These are the practical equivalent of the handbook's "a successful
end-to-end test" and "a test for User Service being unavailable" —
a true multi-process end-to-end test is the manual curl walkthrough above.)

## Section 12 — configuration mini-exercise

Try this yourself to confirm the URL really is external to the Java code:

1. Stop User Service.
2. Change its port: edit `user-service/src/main/resources/application.properties`,
   set `server.port=9091`.
3. Update **only** Order Service's config — edit
   `order-service/src/main/resources/application.properties`,
   set `user.service.base-url=http://localhost:9091`.
4. Restart both services.
5. Call `curl http://localhost:8082/orders/5001` again — it should work,
   and you never touched `UserClient.java` or `RestClientConfig.java`.
6. Put both files back to `8081` / `http://localhost:8081` afterward.

## Connecting today to Day 4

The handbook asks you to map this call onto yesterday's resilience
concepts. This project intentionally does **not** implement timeout,
retry, or a circuit breaker — Day 5's focus is the communication mechanics
themselves — but here's where each would slot in if it did:

| Day 4 pattern | Where it would go here |
|---|---|
| **Timeout** | On the `RestClient` — Spring's `RestClient` delegates to an underlying `ClientHttpRequestFactory`; you'd configure its connect/read timeout, same idea as `order-service`'s `paymentRestTemplate` in the Day 1–4 platform project. |
| **Retry** | Around `UserClient.getUser(...)`, limited to a few attempts, only for exceptions that look transient (connection refused, timeout) — not for a legitimate 4xx. |
| **Circuit breaker** | Also around `UserClient.getUser(...)`, so repeated failures stop hitting User Service for a cooldown period instead of retrying forever. |
| **Fallback** | Already partially here in spirit — `GlobalExceptionHandler` turns any `RestClientException` into a clear 503, never a raw stack trace. A "real" fallback would live at the same layer, potentially returning cached/default data instead of just an error. |

If you want to see all four of these actually implemented and wired up
with Resilience4j against a deliberately-breakable dependency, that's
exactly what Day 4 built in the `one-enterprise-platform` project
(`order-service`'s `PaymentServiceClient` + `RESILIENCE.md`).

## What changed between Section 5 and Section 11 in this repo

- `RestClientConfig` in this project already reflects **Section 11's**
  fix (externalized `user.service.base-url`), not Section 5's hard-coded
  version — so you're looking at the "after" state. If you want to
  experience the "before" state the handbook describes, try hard-coding
  `.baseUrl("http://localhost:8081")` directly in `RestClientConfig`
  first, get it working, and then refactor to the externalized version
  yourself — that refactor *is* the Section 11 exercise.
