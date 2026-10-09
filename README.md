# Microservices Project — CI/CD, Kubernetes & Monitoring

## 📌 Project Overview

This project demonstrates how to build, test, scan, package, deploy, and monitor a Java Spring Boot microservices application using a DevOps workflow.

The application consists of Product, Order, User, and Gateway services. Jenkins automates the CI/CD pipeline, Maven builds and tests the services, SonarQube performs code analysis, Trivy scans container images, Docker packages the applications, and Helm deploys them to Kubernetes. Prometheus and Grafana provide monitoring and visualization.

The project provides hands-on experience with the software delivery lifecycle, containerization, orchestration, security scanning, and application monitoring.

---

## 🏗️ Architecture

```text
                         Developer
                             |
                             v
                      +--------------+
                      |    GitHub    |
                      | Source Code  |
                      +------+-------+
                             |
                             v
                      +--------------+
                      |    Jenkins   |
                      |   CI / CD    |
                      +------+-------+
                             |
             +---------------+----------------+
             |               |                |
             v               v                v
       +-----------+   +------------+   +------------+
       |  Maven    |   | SonarQube  |   |   Trivy    |
       | Build/Test|   | Code Scan  |   | Image Scan |
       +-----+-----+   +------------+   +------------+
             |
             v
       +----------------+
       | Docker Images  |
       |  Docker Hub    |
       +-------+--------+
               |
               v
       +---------------------+
       | Kubernetes /        |
       | Minikube Cluster    |
       |                     |
       |  +---------------+  |
       |  | Gateway       |  |
       |  +-------+-------+  |
       |          |          |
       |    +-----+-----+    |
       |    |     |     |    |
       |    v     v     v    |
       | Product Order User  |
       | Service Service Svc |
       +----------+----------+
                  |
          +-------+--------+
          |                |
          v                v
   +-------------+   +-------------+
   | Prometheus  |-->|   Grafana   |
   | Metrics     |   | Dashboards  |
   +-------------+   +-------------+
```

*The diagram is a high-level representation. Exact monitoring targets and Kubernetes resource names depend on your deployment configuration.*

---

## ☁️ Technologies and Tools Used

- **Git and GitHub** — source control and repository hosting
- **Java 21 and Spring Boot** — application development
- **Maven** — build and test automation
- **Jenkins** — CI/CD pipeline
- **SonarQube** — static code analysis and code quality reporting
- **Docker** — container image creation
- **Docker Hub** — container image registry
- **Trivy** — container image vulnerability scanning
- **Docker Compose** — local multi-container deployment
- **Kubernetes and Minikube** — container orchestration and local cluster
- **Helm** — Kubernetes application packaging and deployment
- **Prometheus** — metrics collection and monitoring
- **Grafana** — monitoring dashboards and visualization
- **Linux and Bash** — development and automation environment

---

## 🧩 Microservices

| Service | Purpose | Local port |
|---|---|---:|
| Product Service | Product-related APIs | 8081 |
| Order Service | Order-related APIs | 8082 |
| User Service | User-related APIs | 8083 |
| Gateway Service | Entry point for routing requests to services | 8090 |

The Gateway Service is used to access the application APIs through a common entry point.

Example local endpoints, when the services are running:

```text
http://localhost:8090/products
http://localhost:8090/orders
http://localhost:8090/users
```

Health endpoints used by the pipeline:

```text
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
http://localhost:8090/actuator/health
```

These endpoints require the corresponding routes and Spring Boot Actuator configuration to be enabled.

---

## 🔄 CI/CD Pipeline with Jenkins

Jenkins automates the build, validation, image publication, and deployment workflow.

Typical pipeline stages:

1. **Checkout** — retrieve the source code from GitHub.
2. **Build and Test** — run Maven build and test commands for each service.
3. **SonarQube Analysis** — analyze source code and publish code-quality results.
4. **Docker Build** — build a Docker image for each microservice.
5. **Trivy Security Scan** — scan images for HIGH and CRITICAL vulnerabilities according to the configured pipeline policy.
6. **Docker Login** — authenticate to Docker Hub using Jenkins credentials.
7. **Docker Push** — publish version-tagged images to Docker Hub.
8. **Docker Cleanup** — stop or remove the previous local Compose deployment, when configured.
9. **Deploy with Docker Compose** — start the local containerized application.
10. **Wait for Services** — allow containers time to start.
11. **Health Verification** — check service health endpoints.
12. **API Verification** — send test requests through the gateway.
13. **Deploy to Kubernetes with Helm** — upgrade or install the application release.
14. **Monitoring Verification** — verify Prometheus targets and Grafana dashboards/alerts as configured.

The precise stage order and behavior are defined by the repository's `Jenkinsfile`.

---

## 🐳 Docker and Docker Compose

Each microservice is packaged into a Docker image. Images are tagged with the Jenkins build number and pushed to Docker Hub.

Example image names:

```text
docker.io/mounikar04/product-service:<BUILD_NUMBER>
docker.io/mounikar04/order-service:<BUILD_NUMBER>
docker.io/mounikar04/user-service:<BUILD_NUMBER>
docker.io/mounikar04/gateway-service:<BUILD_NUMBER>
```

Replace `<BUILD_NUMBER>` with the actual tag used by your build.

Useful Docker Compose commands:

```bash
docker compose up -d
docker compose ps
docker compose logs --tail=100
docker compose down
```

Use `docker compose logs <service-name>` to investigate an individual service.

---

## 🔐 Code Quality and Container Security

### SonarQube

SonarQube analyzes source code to help identify code-quality issues and potential bugs. Review the analysis results in the SonarQube dashboard after a successful scan.

### Trivy

Trivy scans the built container images for known vulnerabilities. The Jenkins pipeline is configured to report HIGH and CRITICAL findings with `--exit-code 0`; therefore, those findings alone do not fail the build under that setting. Change the exit-code policy if you want the pipeline to fail when vulnerabilities are found.

Credentials such as Docker Hub tokens should be stored in Jenkins Credentials rather than committed to the repository.

---

## ☸️ Kubernetes Deployment with Helm

The project uses the Helm chart in `microservices-chart/` to deploy the microservices to Kubernetes. The Jenkins pipeline runs `helm upgrade --install` and waits for the release to become ready, subject to the configured timeout.

Useful commands:

```bash
minikube status
kubectl get nodes
kubectl get pods
kubectl get services
helm list
helm status microservices-project
```

To inspect a pod:

```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

To check all namespaces:

```bash
kubectl get pods -A
kubectl get services -A
```

The exact Kubernetes service types, replica counts, and image tag configuration are defined by the Helm chart and its values.

---

## 📊 Monitoring with Prometheus and Grafana

Prometheus and Grafana are included in the project monitoring setup.

- **Prometheus** collects metrics from configured scrape targets.
- **Grafana** connects to a data source such as Prometheus to visualize metrics in dashboards.
- **Alerts**, when configured, can notify users about selected conditions such as service availability or resource usage.

### Verify monitoring components

```bash
kubectl get pods -A | grep -Ei 'prometheus|grafana'
kubectl get services -A | grep -Ei 'prometheus|grafana'
```

If the services are installed in Kubernetes and their service names/ports match your setup, you can access them locally using port forwarding. First inspect the actual namespace and service names with the commands above, then substitute those values below:

```bash
kubectl -n <namespace> port-forward svc/<prometheus-service> 9090:9090
kubectl -n <namespace> port-forward svc/<grafana-service> 3000:80
```

Run each port-forward command in a separate terminal. Open the corresponding local address:

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

The Grafana service's target port may differ from `80`; check `kubectl get service -n <namespace> <grafana-service> -o yaml` and use the correct service port. Sign in using the credentials configured for your installation; do not store passwords in this README.

### Monitoring checks

- Confirm the Prometheus targets are `UP`.
- Confirm Grafana has a working Prometheus data source.
- Check that dashboards display the expected metrics.
- If alerts are configured, verify the alert rules and notification contact point.
- Confirm the relevant application metrics are exposed and included in the Prometheus scrape configuration; running Prometheus and Grafana alone does not automatically provide every application metric.

---

## 📁 Project Structure

```text
microservices-project/
├── Jenkinsfile
├── README.md
├── docker-compose.yml
├── microservices-chart/
├── product-service/
├── order-service/
├── user-service/
└── gateway-service/
```

Monitoring manifests or configuration files may be stored in a separate directory, depending on how Prometheus and Grafana were installed. Update this tree to match the actual repository contents.

---

## 🩺 Health Checks and Troubleshooting

The pipeline checks the Actuator health endpoint for each service and sends API requests through the gateway. If a check fails, investigate the service before assuming deployment succeeded.

### Useful troubleshooting commands

```bash
# Local Compose deployment
docker compose ps
docker compose logs --tail=100

# Kubernetes deployment
kubectl get pods -A
kubectl get events --sort-by=.lastTimestamp
kubectl describe pod <pod-name>
kubectl logs <pod-name>

# Helm release
helm status microservices-project
helm history microservices-project

# Monitoring components
kubectl get pods -A | grep -Ei 'prometheus|grafana'
kubectl get services -A | grep -Ei 'prometheus|grafana'
```

Common areas to check include image tags, container startup logs, port mappings, health endpoint configuration, Kubernetes events, Helm values, Prometheus scrape targets, and Grafana data-source settings.

---

## 💰 Cost and Resource Management

This project is primarily intended for learning and development. Local Docker containers, Minikube workloads, and monitoring components consume laptop CPU, memory, disk space, and network bandwidth.

Clean up resources when they are no longer needed:

```bash
docker compose down
```

To stop Minikube:

```bash
minikube stop
```

To delete the Minikube cluster, only when you no longer need its workloads and data:

```bash
minikube delete
```

Deleting the cluster removes its Kubernetes workloads and cluster state. Confirm whether any persistent data needs to be backed up first.

---

## 🎯 Key Learning Outcomes

Through this project, I gained practical experience with:

- Building and testing Java Spring Boot microservices with Maven
- Managing source code with Git and GitHub
- Automating CI/CD with Jenkins
- Performing code analysis with SonarQube
- Building and publishing Docker images
- Scanning container images with Trivy
- Running multi-container applications with Docker Compose
- Deploying applications to Kubernetes using Helm
- Verifying application health and APIs
- Collecting and visualizing metrics with Prometheus and Grafana
- Investigating build, container, deployment, and monitoring issues
- Managing application versions through build-number image tags

---

## 🔄 Future Improvements

Possible improvements include:

- Enforce SonarQube quality gates in the pipeline
- Fail builds based on an agreed Trivy vulnerability policy
- Add automated integration and end-to-end tests
- Add resource requests, limits, and autoscaling to Kubernetes
- Configure HTTPS and secure ingress access
- Add persistent storage and backups where needed
- Improve Prometheus alert rules and Grafana notification routing
- Add dashboards for application latency, error rates, and resource usage
- Store deployment configuration and secrets securely
- Add rollback and deployment verification steps

---

## 👩‍💻 Project Summary

The `microservices-project` demonstrates an end-to-end DevOps workflow for a Java Spring Boot microservices application. Jenkins automates builds, tests, code analysis, container image creation, security scanning, and deployment. Docker and Docker Compose support containerized execution, Kubernetes and Helm manage the cluster deployment, and Prometheus with Grafana provides monitoring and visualization.

The project focuses on CI/CD automation, containerization, orchestration, security scanning, service health verification, monitoring, and practical troubleshooting.
