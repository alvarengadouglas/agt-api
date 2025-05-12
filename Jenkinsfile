def defaultBuildImage = (BRANCH_NAME == "master")

pipeline {

    environment {
        IMAGETAG = "${env.GIT_COMMIT}"
        DOCKERLOCATION = "agentsmanagement-backend"
    }

    agent any
    tools {
            jdk 'JDK11'
        }
    parameters {
        booleanParam(name: 'SONAR_CHECK', defaultValue: false, description: 'Run Sonar check for source code')
        booleanParam(name: 'BUILD_DOCKER_IMAGE', defaultValue: defaultBuildImage, description: 'Build docker image')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip tests. Use it only in very special cases')
      }

    stages {

        stage ('Build') {
            steps {

                              sh './mvnw clean install -DskipTests=${SKIP_TESTS}'

            }
        }

        stage ('Sonar check') {
               when {
                                   expression {
                                            params.SONAR_CHECK == true
                                            }
                                    }
            steps {

                              sh './mvnw sonar:sonar'

            }
        }

        stage('Docker build') {
            when {
                expression {
                    params.BUILD_DOCKER_IMAGE == true
                }
            }
            steps {
                script{
                    dockerapp = docker.build("${DOCKERLOCATION}:${IMAGETAG}", " -f Dockerfile .")
                }
            }
        }


        stage('Docker image push to registry'){
            when {
                expression {
                    params.BUILD_DOCKER_IMAGE == true
                }
            }
            steps{
                script{
                    docker.withRegistry('http://pulpobetregistry.azurecr.io', 'pulpobet-container-registry'){
                        dockerapp.push("${IMAGETAG}")
                    }
                }
            }
        }
        stage('delete local image') {
            when {
                expression {
                    params.BUILD_DOCKER_IMAGE == true
                }
            }
            steps{
                sh 'docker image rm $DOCKERLOCATION:$IMAGETAG'
            }
        }
}
}