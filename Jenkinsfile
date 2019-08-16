node {

	def scmInfo

	stage('Clone repository') {
		scmInfo = checkout scm
	}

	stage('Run Maven build') {

		def maven = docker.image('maven:3.3-jdk-8')
		maven.pull()
		maven.inside {
			sh 'mvn install'
		}
	}
}
