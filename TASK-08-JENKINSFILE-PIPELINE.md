# Task 8 — Jenkinsfile Pipeline

## 1. Objective
The objective of this task is to implement a Jenkinsfile Pipeline for the Travel Insurance Claim System. This automates the continuous integration workflow using the Maven Wrapper and prepares for environment-specific deployments via a parameterized pipeline.

## 2. Jenkinsfile Location
The `Jenkinsfile` has been created at the root level of the `travel-insurance-claim-system` repository.

## 3. Pipeline Stages
The declarative pipeline consists of the following stages:
- **Checkout**: Automatically checks out the configured Git repository and branch.
- **Build**: Compiles the project using the Maven Wrapper (`call mvnw.cmd clean compile`).
- **Test**: Executes automated tests using the Maven Wrapper (`call mvnw.cmd test`). If tests fail, the pipeline fails.
- **Package**: Packages the Spring Boot application as a JAR, skipping tests since they ran in the previous stage (`call mvnw.cmd package -DskipTests`).
- **Deploy**: A separate placeholder stage that simulates deployment preparation.

## 4. Maven Wrapper Usage
The pipeline uses the existing Windows batch commands for the Maven Wrapper (`mvnw.cmd`). This ensures builds are consistent and a global Maven installation is not required on the Jenkins agent.

## 5. Parameterized Environment
A Jenkins pipeline parameter named `DEPLOY_ENV` is configured with the following choices:
- `dev`
- `staging`
- `prod`

The selected value is available throughout the pipeline and is used to identify the target deployment environment.

## 6. Current Deployment Placeholder
Currently, the **Deploy** stage serves as a placeholder. It simply prints that deployment preparation has completed for the selected `DEPLOY_ENV` to the console output. Actual deployment to an application server (e.g., Tomcat) will be configured in subsequent steps. No real deployment infrastructure has been configured yet.

## 7. Expected Pipeline Flow
1. User manually triggers the pipeline or it is triggered by a webhook.
2. User selects the `DEPLOY_ENV` parameter (dev, staging, or prod).
3. The pipeline checks out the source code.
4. The pipeline compiles the source code.
5. The pipeline runs automated tests.
6. The pipeline packages the application into an executable JAR.
7. The pipeline outputs a deployment preparation message for the chosen environment.

## 8. Verification Checklist
- [ ] `Jenkinsfile` created at the repository root.
- [ ] Pipeline is declarative (`pipeline { ... }`).
- [ ] `DEPLOY_ENV` parameter configured with `dev`, `staging`, and `prod` choices.
- [ ] Stages (Checkout, Build, Test, Package, Deploy) are clearly defined.
- [ ] Windows-compatible commands (`call mvnw.cmd ...`) are used.
- [ ] Tests and packaging are separated into distinct stages.
- [ ] Deploy stage prints the selected environment.

## 9. Evidence Checklist
- [ ] `Jenkinsfile` committed to the repository (to be done).
- [ ] Jenkins Pipeline job configured to use the `Jenkinsfile` (to be done).
- [ ] Pipeline execution logs showing successful completion of all stages (to be done).
- [ ] `DEPLOY_ENV` value visible in the Jenkins console output (to be done).
