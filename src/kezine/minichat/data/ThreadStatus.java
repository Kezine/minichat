package kezine.minichat.data;

/**
 * Gère les diffèrent status dans lequels les threads de l'application peuvent se trouver.
 * @author Kezine
 */
 public enum ThreadStatus
 {
    INITED("Innited"),RUNNING("Running"),STOPPING("Stopping"),FAILED("Failed"),STOPPED("Stopped"),STOPPED_WITH_ERROR("StoppedWithError");
    private String name;
    private ThreadStatus(String typeName)
    {
            name = typeName;
    }
    /**
     * @return Le nom du type
     */
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
