# Deploy a Spring Boot App on AWS ECS Cluster
 1. Create a Dockerfile for your Spring Boot application that specifies the base image, dependencies, and application code.
 2. Build a Docker image of your Spring Boot application using the Docker CLI.
 3. Tag the Docker image with the name of your ECR repository and push the Docker image to your ECR repository using the AWS CLI.
 4. Create an ECS task definition that defines the Docker container for your Spring Boot application, including the Docker image name and the required CPU and memory resources.

 5. Create an ECS service that runs your ECS task definition, specifying the desired number of tasks and the load balancer configuration for distributing traffic to the tasks.
 6. Test your Spring Boot application by accessing the application deployed on the ECS service.