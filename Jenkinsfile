pipeline{
	agent any
	stages{
		stage('Build') {
			agent{
        		        docker{
                       			image 'maven:3-openjdk-14'
                        		args '-v /root/.m2:/root/.m2'
                		}
        		}
			steps{
				sh 'mvn -B -DskipTests clean package'		
			}
		}
		stage('Deployment'){
			steps{
				sh 'chmod +x ./deploy.sh && ./deploy.sh'
			}
		}
	}
}
