package kiss.util;

import java.lang.reflect.Method;

import kiss.API.Generator;
import kiss.API.Listener;

public class AutoListener<Message> implements Listener<Message> {
    private Object object;
    private Method method;

    public AutoListener(Class<Message> type,  Object _object) {
        try {
            object=_object;
            String name = type.getName();
            int pos = name.lastIndexOf('.');
            String Name=name.substring(pos+1, pos+2).toUpperCase()
                +name.substring(pos+2);
            
            String methodName = "onReceive" + Name;
            method = object.getClass().getDeclaredMethod(methodName, type);
            method.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ignored!");
        }
    }
    
    @Override
    public void receive(Message message) {
        try {
            method.invoke(object,message);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ignored!");
        }
    }
}
