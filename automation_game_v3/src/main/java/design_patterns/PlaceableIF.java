package design_patterns;

public interface PlaceableIF {
    void setNext(GameObject next);
    void setPrev(GameObject prev);
    void reqDelete();
    void unsetNext(GameObject gameObject);
    void unsetPrev(GameObject gameObject);
    boolean incomingSendRequest(GameObject resource, GameObject placeable);
    void  incomingGetRequest();
    void performAction();
}
