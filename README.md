```markdown
# Net Centric Lab 4 - Distributed Weather System

This project simulates a distributed system with Weather Stations sending data to a Central Station via Kafka, orchestrated using Docker and Kubernetes.

## Prerequisites
* Java 17+ & Maven
* Docker
* Minikube & Kubectl (for Part F)

## Build the Project
Before running any part, you must compile the Java code.
```bash
mvn clean package

```

---

## Part D: Docker Compose (Local Deployment)

This runs the full system (Zookeeper, Kafka, Postgres, Weather Stations, Central Station) locally.

1. Navigate to the folder:
```bash
cd part_d

```


2. Start the system:
```bash
docker-compose up --build

```


3. To stop: Press `Ctrl+C` and run `docker-compose down`.

---

## Part E: Data Analysis

**Note:** The system from Part D must be running.

1. Open a terminal into the Postgres container:
```bash
docker exec -it weather-db psql -U postgres -d weather_db

```


2. Run the analysis queries found in `part_e/queries.sql` to view:
* Battery Status Distribution.
* Dropped Messages Count.



*Screenshots of expected results are located in the `part_e/` folder.*

---

## Part F: Kubernetes Deployment (Minikube)

This runs the system inside a Kubernetes cluster.

1. **Start Minikube:**
```bash
minikube start
eval $(minikube docker-env)

```


2. **Build the Docker Images:**
(Run these commands from the **root** of the repository, not inside `part_f`)
```bash
docker build -t my-weather-station:v1 -f part_f/Dockerfile.weather .
docker build -t my-central-station:v1 -f part_f/Dockerfile.central .

```


3. **Deploy to Kubernetes:**
```bash
kubectl apply -f part_f/k8s-deployment.yaml

```


4. **Verify:**
```bash
kubectl get pods

```


*Wait until all pods show `STATUS: Running`.*
5. **Stop:**
```bash
minikube stop

```



```

```
