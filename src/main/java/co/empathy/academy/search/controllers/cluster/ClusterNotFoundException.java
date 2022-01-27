package co.empathy.academy.search.controllers.cluster;

public class ClusterNotFoundException extends Exception {
    public ClusterNotFoundException(Throwable exception) {
        super("The cluster name is unavailable", exception);
    }
}
