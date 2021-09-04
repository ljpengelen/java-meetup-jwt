def withDockerNetwork(Closure inner) {
  try {
    networkId = UUID.randomUUID().toString()
    sh "docker network create ${networkId}"
    inner.call(networkId)
  } finally {
    sh "docker network rm ${networkId}"
  }
}

pipeline {
  agent none

  stages {
    stage("Test JWT-based App") {
      agent any

      steps {
        script {
          def database = docker.build("database", "database")
          def app = docker.build("jwt-based-app", "-f back-end-jwt/dockerfiles/ci/Dockerfile .")

          withDockerNetwork{ n ->
            database.withRun("--network ${n} --name database") { c ->
              app.inside("""
                --network ${n}
                -e 'SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/java_meetup_test'
              """) {
                dir("back-end-jwt") {
                  sh "mvn verify"
                }
              }
            }
          }
        }
      }
    }

    stage("Test Session-based App") {
      agent any

      steps {
        script {
          def database = docker.build("database", "database")
          def app = docker.build("session-based-app", "-f back-end-session/dockerfiles/ci/Dockerfile .")

          withDockerNetwork{ n ->
            database.withRun("--network ${n} --name database") { c ->
              app.inside("""
                --network ${n}
                -e 'SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/java_meetup_test'
              """) {
                dir("back-end-session") {
                  sh "mvn verify"
                }
              }
            }
          }
        }
      }
    }
  }
}
