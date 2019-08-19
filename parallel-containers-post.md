# Running multiple Docker containers in parallel with Jenkins

This morning, I was looking for a way to run multiple Docker containers in parallel with Jenkins.
Even though this seemed like a common use case to me, it took me a while to find all information I needed and piece it together.
As you know, the only design pattern you need is copy-paste.
I wrote this post to allow you and my future self to copy-paste some useful snippets from a Jenkinsfile.

Suppose you have a Java app that requires a PostgreSQL database.
If you want to run a few integration tests for that app, you'll probably need this database to be accessible as well.
To make it possible for Jenkins to run these integration tests for you, you could just install PostgreSQL on the machine running Jenkins, create the necessary databases and users, and call it a day.
However, I'd rather keep each app in its own Docker container and only have apps running when they're needed.

Consider the following Jenkinsfile:

```
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
    stage("test") {
      agent any

      steps {
        script {
          def database = docker.build("database", "database")
          def app = docker.build("app", "-f dockerfiles/ci/Dockerfile .")

          withDockerNetwork{ n ->
            database.withRun("--network ${n} --name database") { c ->
              app.inside("""
                --network ${n}
                -e 'SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/test'
              """) {
                sh "mvn verify"
              }
            }
          }
        }
      }
    }
  }
}
```

The function `withDockerNetwork` (copy-pasted from [Ryan Desmon](https://issues.jenkins-ci.org/browse/JENKINS-49567)) creates and eventually deletes a Docker network with a random name.
After creating the network, it calls a block of code of your choice and provides it with this random name.
After the block of code has finished, the network is deleted.

The statement `docker.build("database", "database")` builds a Docker image named "database" with the context `database`.
The statement `docker.build("app", "-f dockerfiles/ci/Dockerfile .")` builds a Docker image named "app" from the Dockerfile `dockerfiles/ci/Dockerfile` with context `.`.

Once both images are built, containers based on these images are started and connected to the same network, allowing them to communicate.
The arguments `--network ${n}` are used to connect both containers to the network.
The container for the database is given a name explicitly with the argument `--name database`, so that we can point the app to it.
The latter is achieved by setting an environment variable with the argument `-e 'SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/test'`.
This last step is specific to Spring.
You'll probably need to do something completely different for your own use case.

Once both containers are running, the tests for the app are executed by the step `sh "mvn verify"`.
This step is specific to Java and Maven and is unrelated to running containers in parallel.

If you want to see this in action, take a look at https://github.com/ljpengelen/java-meetup-jwt.
The example above is a simplified version of the Jenkinsfile used for this project.
