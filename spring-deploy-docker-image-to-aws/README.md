# Deploy a Spring Boot App on AWS ECS Cluster
* Create a Dockerfile for the Spring Boot application that specifies the base image, dependencies, and application code.
* Build a Docker image of the Spring Boot application using the Docker CLI.
    
```
docker build -t my-application .
```

* Tag the Docker image with the name of the ECR repository and push the Docker image to ECR repository using the AWS CLI.

```
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 644805545910.dkr.ecr.us-east-1.amazonaws.com

docker tag my-application:latest 644805545910.dkr.ecr.us-east-1.amazonaws.com/howtodoinjava:latest

docker push 644805545910.dkr.ecr.us-east-1.amazonaws.com/howtodoinjava:latest
```

* Create an ECS task definition that defines the Docker container for your Spring Boot application, including the Docker image name and the required CPU and memory resources.
* Create an ECS service that runs your ECS task definition, specifying the desired number of tasks and the load balancer configuration for distributing traffic to the tasks.
* Test your Spring Boot application by accessing the application deployed on the ECS service.