package edu.virginia.engine.display;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventDispatcher;
import edu.virginia.engine.events.IEventListener;
import java.util.ArrayList;

public class Coin extends Sprite implements IEventDispatcher
{

    private ArrayList<IEventListener> listeners = new ArrayList<IEventListener>();
    public Coin(String id, String imageFileName)
    {
        super(id, imageFileName);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void addEventListener(IEventListener listener, String eventType)
    {
        // TODO Auto-generated method stub
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(IEventListener listener, String eventType)
    {
        // TODO Auto-generated method stub
        listeners.remove(listener);
    }

    @Override
    public void dispatchEvent(Event event)
    {
        // TODO Auto-generated method stub
        System.out.println("Notify All Listeners");
        for(IEventListener listener:listeners) {
            listener.handleEvent(event);
        }
    }

    @Override
    public boolean hasEventListener(IEventListener listener, String eventType)
    {
        // TODO Auto-generated method stub
        return false;
    }



}
