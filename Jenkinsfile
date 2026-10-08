pipeline {
  
  agent any

    environment {
      DOCKERHUB_USERNAME = 'mounikar04'
      
      PRODUCT_IMAGE = "${DOCKERHUB_USERNAME}/product-service"
      ORDER_IMAGE = "${DOCKERHUB_USERNAME}/order-service"
      USER_IMAGE = "${DOCKERHUB_USERNAME}/user-service"
      GATEWAY_IMAGE = "${DOCKERHUB_USERNAME}/gateway-service"

      IMAGE_TAG = "${BUILD_NUMBER}"
      }

    stages {
   
      stage('Checkout') {
        steps {
            checkout scm
             }
       }
     
      stage('Build & Test all services') {
        steps {
            sh '''
                  set -e

                  echo "Building product-service..."
                  cd product-service
                  mvn clean package
                  cd ..

                  echo "Building order-service..."
                  cd order-service
                  mvn clean package
                  cd ..

                  echo "Building user-service...."
                  cd user-service
                  mvn clean package
                  cd ..
 
                  echo "Building gateway-service"
                  cd gateway-service
                  mvn clean package
                  cd ..

                  echo "All services built & tested successfully!"
                  '''
                 }
              }
    
    stage('Sonarqube Code Analysis') {
      steps {
         withSonarQubeEnv('SonarQube') {

            sh '''
                set -e

                echo "Running SonarQube analysis for product-service"
                cd product-service
                mvn sonar:sonar
                cd .. 

                echo "Running SonarQube analysis for order-service"
                cd order-service
                mvn sonar:sonar
                cd ..

                echo "Running SonarQube analysis for user-service"
                cd user-service
                mvn sonar:sonar
                cd ..

                echo "Running SonarQube analysis for gateway-service"
                cd gateway-service
                mvn sonar:sonar
                cd ..

                echo "SonarQube analysis completed successfully!"
              '''
             }
          }

    stage('Docker Build') {
      steps {
        sh '''
             docker build -t ${PRODUCT_IMAGE}:${IMAGE_TAG} ./product-service
             docker build -t ${ORDER_IMAGE}:${IMAGE_TAG} ./order-service
             docker build -t ${USER_IMAGE}:${IMAGE_TAG} ./user-service
             docker build -t ${GATEWAY_IMAGE}:${IMAGE_TAG} ./gateway-service
           '''
            }
         }

    stage('Trivy Security Scan') {
      steps {
         sh '''
             set -e

             echo "Scanning product-service image..."
             trivy image --severity HIGH,CRITICAL --exit-code 0 ${PRODUCT_IMAGE}:${IMAGE_TAG}

             echo "Scanning order-service image..."
             trivy image --severity HIGH,CRITICAL --exit-code 0 ${ORDER_IMAGE}:${IMAGE_TAG}

             echo "Scanning user-service image..."
             trivy image --severity HIGH,CRITICAL --exit-code 0 ${USER_IMAGE}:${IMAGE_TAG}

             echo "Scanning gateway-service image..."
             trivy image --severity HIGH,CRITICAL --exit-code 0 ${GATEWAY_IMAGE}:{IMAGE_TAG}

             echo "Trivy security scanning completed!"
            '''
           }
       }
    
    stage('Docker login') {
      steps {
        withCredentials([usernamePassword(
                                        credentialsId: 'dockerhub-creds',
                                        usernameVariable: 'DOCKER_USERNAME',
                                        passwordVariable: 'DOCKER_PASSWORD' )
               ]) {
                   sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                      '''
                   }
           }
    }
 
    stage('Docker push') {
      steps {
        sh '''
             docker push ${PRODUCT_IMAGE}:${IMAGE_TAG} 
             docker push ${ORDER_IMAGE}:${IMAGE_TAG}               
             docker push ${USER_IMAGE}:${IMAGE_TAG}
             docker push ${GATEWAY_IMAGE}:${IMAGE_TAG}
            '''
            }
         }
 
    stage('Docker cleanup') {
      steps {
        sh '''
             echo "Cleaning previous microservices deployment"

             docker compose down --remove-orphans || true
       
             docker rm -f microservices-project-gateway-service-1 microservices-project-product-service-1 microservices-project-order-service-1 microservices-project-user-service-1

             docker network rm microservices-network || true

             echo "Docker cleanup completed"
             '''
            }
        }

    stage ('Deploy New Version') {
      steps {
        sh ''' 
             set -e

             echo "Deploying version: ${IMAG_TAG}"
   
             echo "Pulling new Docker images..."

             echo "Starting new version..."
 
             IMAGE_TAG=${IMAGE_TAG} docker compose up -d --build

             echo "Deployment command completed."
           '''
            }
        }

    stage ('Wait for services') {
      steps {
        sh '''
             echo "Waiting for services to become healthy..."

             sleep 30

             docker compose ps
           '''
           }
        }

    stage ('Health Verification') {
      steps {
        sh ''' 
             echo "Checking Product service.."             
             curl --fail http://localhost:8081/actuator/health

             echo "Checking Order service.." 
             curl --fail http://localhost:8082/actuator/health

             echo "Checking User service.." 
             curl --fail http://localhost:8083/actuator/health

             echo "Checking Gateway service.." 
             curl --fail http://localhost:8090/actuator/health

             echo "All services are Healthy!"
           '''
           }
        }
 
    stage('API Verification') {
      steps {
        sh '''
             echo "Testing Product API..."
             curl --fail http://localhost:8090/products

             echo "Testing Order API..."
             curl --fail http://localhost:8090/orders

             echo "Testing Users API..."
             curl --fail http://localhost:8090/users

             echo "All APIs are working!"
           '''
           }
        }

   stage('Deploy to Kubernetes with Helm') {
     steps {
       sh '''
            echo "Deploying microservices to Kubernetes using Helm..."
     
            helm upgrade --install microservices-project \
              ./microservices-chart \
              --set image.tag=${IMAGE_TAG} \
              --wait \
              --timeout 5m

            echo "Helm deployment completed successfully!"

            kubectl get pods
            kubectl get services
          '''
         }
      }    
   }

   post {

         success {
            echo "======================================================"
            echo "MICROSERVICES CI/CD SUCCESSFUL"
            echo "======================================================"
            echo "Build number: ${BUILD_NUMBER}"
            }
 
         failure {
            echo "======================================================"
            echo "MICROSERVICES CI/CD FAILED"
            echo "======================================================"
            echo "Check the Jenkins console output"
            }

         always {
            sh 'docker logout || true'
          }
      }
    }

  
