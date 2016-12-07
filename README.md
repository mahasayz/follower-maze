# Back-end Developer Challenge: Follower Maze

This document outlines the solution for the Back-end Developer Challenge

### Solution
The solution is a Maven-based project and the JAR-file can be built as follows.

```
$ mvn clean compile package
```

The packaged JAR-file has already been made available in this repository and can
be run using the following command.
```
$ java -cp target/follower-maze-1.0-SNAPSHOT.jar com.soundcloud.followermaze.Main 9090 9099
```

The JavaDocs for this solution have also been duly uploaded the repository and can
be found in the `javadocs` folder

### Test Cases

The following test strategies have been applied against the solution:
* Testing invalid format for incoming events
* Testing the event-listener for incoming events
* Testing the event-handler for handling events in a orderly manner
* Testing the event-handler to ensure that messages are flushed to the respective client's output stream