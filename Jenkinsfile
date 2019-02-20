def initM2 () {
    //初始化构建容器中的m2配置, 其中172.18.28.37:31269是tdc上安装的nexus3的地址
    //admin与admin123是nexus的admin用户, 也可以配置其他有权限的用户
    sh '''#!/bin/bash
    cat <<EOF > /root/.m2/settings.xml
    <?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:// @ts-ignore
        xsi="http://www.w3.org/2001/XMLSchema-instance">
            <servers>
            <server>
            <username>admin</username>
            <password>admin123</password>
            <id>release</id>
            </server>
            <server>
            <username>admin</username>
            <password>admin123</password>
            <id>snapshots</id>
            </server>
            <server>
            <username>admin</username>
            <password>admin123</password>
            <id>plugin</id>
            </server>
            <server>
            <username>admin</username>
            <password>admin123</password>
            <id>plugin-snapshots</id>
            </server>
            </servers>
            <mirrors>
            <mirror>
            <id>release</id>
            <name>nexus repo for dependencies</name>
                                 <url>http://172.18.28.37:31269/repository/maven-public</url>
    <mirrorOf>*</mirrorOf>
    </mirror>
    </mirrors>
    <profiles>
    <profile>
    <repositories>
    <repository>
    <snapshots>
    <enabled>false</enabled>
    </snapshots>
    <id>release</id>
    <name>maven-releases</name>
    <url>http://172.18.28.37:31269/repository/maven-releases</url>
</repository>
<repository>
    <snapshots />
    <id>snapshots</id>
    <name>maven-snapshots</name>
    <url>http://172.18.28.37:31269/repository/maven-snapshots</url>
</repository>
</repositories>
    <pluginRepositories>
    <pluginRepository>
    <snapshots>
    <enabled>false</enabled>
    </snapshots>
    <id>plugin</id>
    <name>maven-releases</name>
    <url>http://172.18.28.37:31269/repository/maven-releases</url>
</pluginRepository>
<pluginRepository>
    <snapshots />
    <id>plugin-snapshots</id>
    <name>plugins-snapshot</name>
    <url>http://172.18.28.37:31269/repository/maven-snapshots</url>
</pluginRepository>
</pluginRepositories>
    <id>artifactory</id>
    </profile>
    </profiles>
    <activeProfiles>
    <activeProfile>artifactory</activeProfile>
    </activeProfiles>
    </settings>
    EOF
    '''
}

pipeline {
    agent {
        //这里的agent的label需要填在jenkins系统配置里添加的Pod的label, 在本例中就是ticket_builder
        label "zjnx"
    }
    environment {
        //该docker url是tdc上安装的harbor的地址
        DOCKER_REPO_URL  = "172.18.28.37:31165"
    }
    stages {
        //定义satge
        stage('CI Build and push snapshot') {
            environment {
                //这里可以定义环境变量
                JAVA_HOME = "/usr/jdk-8u131-linux-x64.tar/jdk1.8.0_131/"
            }
            steps {
                //指定容器, 需要与jenkins系统配置里定义的container名字相同
                container('maven') {
                    initM2()
                    sh '''mvn clean compile'''
                }
            }
        }
        stage('Unit Test && Image Build') {
            environment {
                BUILDER = "postcommit"
                IMAGE_TAG = "master"
                JAVA_HOME = "/usr/jdk-8u131-linux-x64.tar/jdk1.8.0_131/"
            }
            steps {
                container('maven') {
                    sh '''startdocker.sh $DOCKER_REPO_URL &'''
                    chmod +x gradlew && ./gradlew bootRepackage
                    cp build/libs/*.jar src/docker/
                    docker build . -t devops-demo:latest
                    docker tag devops-demo:latest ${DOCKER_REPO_URL}/${BUILDER}/devops-demo:${IMAGE_TAG}
                    docker push ${DOCKER_REPO_URL}/${BUILDER}/devops-demo:${IMAGE_TAG}'''
                }
            }
        }
        stage('Gold Deploy') {
            environment {
                releaseStagingId = "release"
                releaseRepoName = "maven-releases"
                releaseRepoUrl = "http://172.18.28.37:31269/repository/maven-releases"
                snapStagingId = "snapshots"
                snapRepoName = "maven-snapshots"
                snapRepoUrl = "http://172.18.28.37:31269/repository/maven-snapshots"
                BUILDER = "gold"
                IMAGE_TAG = "master"
                JAVA_HOME = "/usr/jdk-8u131-linux-x64.tar/jdk1.8.0_131/"
            }
            steps {
                container('maven') {
                    initM2()
                    sh '''startdocker.sh $DOCKER_REPO_URL &'''

                    sh '''mvn deploy -DskipTests \
                -DdistMgmtStagingId=${releaseStagingId} \
                -DdistMgmtStagingName=${releaseRepoName} \
                -DdistMgmtStagingUrl=${releaseRepoUrl} \
                -DdistMgmtSnapshotsId=${snapStagingId} \
                -DdistMgmtSnapshotsName=${snapRepoName} \
                -DdistMgmtSnapshotsUrl=${snapRepoUrl}
                    '''

                    sh '''docker pull ${DOCKER_REPO_URL}/postcommit/devops-demo:${IMAGE_TAG}
                    docker tag ${DOCKER_REPO_URL}/postcommit/devops-demo:${IMAGE_TAG} ${DOCKER_REPO_URL}/${BUILDER}/devops-demo:${IMAGE_TAG}
                        docker push ${DOCKER_REPO_URL}/${BUILDER}/devops-demo:${IMAGE_TAG}
                    '''
                }
            }
        }
    }
}
