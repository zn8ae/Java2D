package edu.virginia.engine.events;
import java.util.ArrayList;

public class EventDispatcher implements IEventDispatcher
{

    private ArrayList<IEventListener> listeners = new ArrayList<IEventListener>();

    @Override
    public void addEventListener(IEventListener listener, String eventType)
    {
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(IEventListener listener, String eventType)
    {
        // TODO Auto-generated method stub
        listeners.remove(listener);
    }

    //this is to notify all listeners about this event
    @Override
    public void dispatchEvent(Event event)
    {
        System.out.println("Notify All Listeners");
        for(IEventListener listener:listeners) {
            listener.handleEvent(event);
        }

    }

    @Override
    public boolean hasEventListener(IEventListener listener, String eventType)
    {
        return false;
    }



}
