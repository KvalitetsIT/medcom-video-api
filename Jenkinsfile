pipeline {
      agent {
        kubernetes {
          defaultContainer 'docker'
          yaml """
    apiVersion: v1
    kind: Pod
    metadata:
      labels:
        some-label: some-label-value
    spec:
      containers:
      - name: docker
        image: docker
        command:
        - cat
        tty: true
        volumeMounts:
          - name: docker-sock
            mountPath: /var/run/docker.sock
      volumes:
      - name: docker-sock
        hostPath:
          path: /var/run/docker.sock
    """
        }
      }

    stages {
        stage('Initialize') {
            steps{
                script {
                    currentBuild.displayName = "$currentBuild.displayName-${env.GIT_COMMIT}"
                }
            }
        }
        stage('Build And Test') {
            steps {
                script {
                    def maven = docker.image('maven:3-jdk-11')
                    maven.pull()
                    maven.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                        sh 'mvn install'
                    }

                    junit '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'
//                    jacoco changeBuildStatus: true, maximumLineCoverage: '80', minimumLineCoverage: '60', exclusionPattern: '**/org/openapitools/**/*.*'
                }
            }
        }
        stage('Tag Docker Images And Push') {
            steps {
                script {
                    docker.withRegistry('','dockerhub') {
                        image = docker.image("kvalitetsit/medcom-video-api:${env.GIT_COMMIT}")
                        image.push("${env.GIT_COMMIT}")
                        image.push("dev")

                        if(env.TAG_NAME != null && env.TAG_NAME.matches("^v[0-9]*\\.[0-9]*\\.[0-9]*")) {
                            echo "Tagging version"
                            image.push(env.TAG_NAME.substring(1))
                            image.push("latest")
                        }
                    }
                }
            }
        }
    }
}
