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
    
    stage('Docker login') {
      steps {
        withCredentials([usernamePassword(
                                        credentialsId: 'dockerhub-credentials',
                                        usernameVariable: 'DOCKER_USERNAME',
                                        passwordVariable: 'DOCKER_PASSWORD' )
               ]) {
                   sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                      '''
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
 
    stage ('Deploy with Docker Compose') {
      steps {
        sh '''
             docker compose down
             docker compose up -d --build
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
