# Kubernetes for Beginners: Hands-On Guide With Spring Boot

Source code for the article [Kubernetes for Beginners: Hands-On Guide With Spring Boot](https://howtodoinjava.com/?p=31102).

A small Spring Boot REST API (`greeting-api`) that reads its message from a ConfigMap, checks an API key from a
Secret, and counts visits in PostgreSQL running as a StatefulSet with a PersistentVolumeClaim.

## Versions

| Tool | Version |
|---|---|
| Java | 25 (image eclipse-temurin:25-jre) |
| Spring Boot | 4.1.1 |
| PostgreSQL | 18.6 (image postgres:18.6) |
| kind | 0.33.0 (node image kindest/node:v1.37.0) |
| kubectl | 1.37.1 |
| ingress-nginx | controller-v1.15.1 (the project is retired, see the article) |

## Run the tests

The tests start PostgreSQL with Testcontainers, so Docker must be running.

```bash
mvn test
```

## Run on a local kind cluster

```bash
# 1. Cluster with one control plane and two workers
kind create cluster --name greeting --config kind/kind-config.yaml

# 2. Build the jar and the image, then copy the image into the cluster nodes
mvn -DskipTests package
docker build -t greeting-api:1.0 .
kind load docker-image greeting-api:1.0 --name greeting

# 3. Configuration, database, application
kubectl apply -f k8s/configmap.yaml -f k8s/secret.yaml -f k8s/postgres.yaml
kubectl apply -f k8s/deployment.yaml -f k8s/service.yaml
kubectl get pods

# 4. Call the API
kubectl port-forward service/greeting-api 8081:80
curl -s localhost:8081/api/greeting -H 'X-API-Key: k8s-demo-key'

# 5. Ingress (optional)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.15.1/deploy/static/provider/kind/deploy.yaml
kubectl patch deployment ingress-nginx-controller -n ingress-nginx -p '{"spec":{"template":{"spec":{"nodeSelector":{"ingress-ready":"true"}}}}}'
kubectl wait -n ingress-nginx --for=condition=ready pod -l app.kubernetes.io/component=controller --timeout=180s
kubectl apply -f k8s/ingress.yaml
curl -s localhost:8080/api/greeting -H 'X-API-Key: k8s-demo-key'

# 6. Delete everything
kind delete cluster --name greeting
```

For the rolling update in the article, change `<version>` in `pom.xml` to `1.1.0`, build the image as
`greeting-api:1.1`, load it with `kind load` and run
`kubectl set image deployment/greeting-api greeting-api=greeting-api:1.1`.

On a Linux host that still uses cgroup v1, create the cluster with `kind/kind-config-cgroupv1.yaml` instead.

## Files

| File | What it contains |
|---|---|
| `src/main/java/com/howtodoinjava/k8s/GreetingController.java` | `GET /api/greeting`, checks the `X-API-Key` header |
| `src/main/java/com/howtodoinjava/k8s/GreetingProperties.java` | `greeting.message` and `greeting.api-key` |
| `src/main/java/com/howtodoinjava/k8s/VisitRepository.java` | Stores one row per visit with `JdbcClient` |
| `src/main/resources/application.properties` | Local defaults and the health probe endpoints |
| `src/test/java/com/howtodoinjava/k8s/GreetingApiTest.java` | 3 tests against PostgreSQL 18.6 in a container |
| `Dockerfile` | Image `greeting-api` on `eclipse-temurin:25-jre` |
| `k8s/configmap.yaml` | ConfigMap `greeting-config` with the message |
| `k8s/secret.yaml` | Secret `greeting-secret` with the API key and database password |
| `k8s/postgres.yaml` | Headless Service and StatefulSet `postgres` with a 1Gi PersistentVolumeClaim |
| `k8s/deployment.yaml` | Deployment `greeting-api` with probes, resources and a preStop hook |
| `k8s/service.yaml` | ClusterIP Service `greeting-api` on port 80 |
| `k8s/ingress.yaml` | Ingress for the path `/api` |
| `kind/kind-config.yaml` | kind cluster: 1 control plane, 2 workers, host port 8080 to the ingress |
| `kind/kind-config-cgroupv1.yaml` | The same cluster for hosts with cgroup v1 |
