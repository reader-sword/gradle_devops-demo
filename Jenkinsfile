pipeline {
    agent {
        //这里的agent的label需要填在jenkins系统配置里添加的Pod的label, 在本例中就是ticket_builder
        label "zjnx"
    }
    environment {
        //该docker url是tdc上安装的harbor的地址
        DOCKER_REPO_URL  = "172.18.28.37:32702"
    }
    stages {
        stage('Gold Deploy') {
            environment {
                releaseStagingId = "release"
                releaseRepoName = "maven-releases"
                releaseRepoUrl = "http://172.18.28.37:30663/repository/maven-releases"
                snapStagingId = "snapshots"
                snapRepoName = "maven-snapshots"
                snapRepoUrl = "http://172.18.28.37:30663/repository/maven-snapshots"
                BUILDER = "gold"
                IMAGE_TAG = "master"
                JAVA_HOME = "/usr/jdk-8u131-linux-x64.tar/jdk1.8.0_131/"
            }
            steps {
                container('maven') {
                    sh '''startdocker.sh $DOCKER_REPO_URL &'''
                    sh '''chmod +x gradlew && ./gradlew bootRepackage
                    cp build/libs/*.jar src/docker/
                    cd src/docker && docker build . -t devops-demo:latest
                    docker tag devops-demo:latest ${DOCKER_REPO_URL}/${BUILDER}/devops-demo:${IMAGE_TAG}
                    docker push ${DOCKER_REPO_URL}/${BUILDER}/devops-demo:${IMAGE_TAG}'''
                   
                }
            }
        }
    }
}
