package ca.momoperes.onelinereflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionProcessor {

    private String line;
    private Object context;

    public ReflectionProcessor(String line, Object context) {
        this.line = line;
        this.context = context;
    }

    /**
     * Processes the given reflective line
     *
     * @return the resultant value of the reflective line
     */
    public Object process() {
        String tmpPackage = "";
        Object cxt = null;
        String[] sections = line.split("\\.");
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i];
            if (section.equals("$") || section.equals("this")) {
                cxt = context;
            } else if (section.endsWith("()")) { // Method
                section = section.substring(0, section.length() - 2);
                if (i == sections.length - 1)
                    return invokeMethod(cxt, section);
                cxt = invokeMethod(cxt, section);
            } else { // Field & Class
                Object field = invokeField(cxt, section);
                if (field == null) {
                    tmpPackage += section + ".";
                    Object clazz = invokeClass(tmpPackage);
                    if (clazz != null) {
                        if (i == sections.length - 1)
                            return clazz;
                        cxt = clazz;
                    }
                } else {
                    if (i == sections.length - 1)
                        return field;
                    cxt = field;
                }
            }
        }
        return cxt;
    }

    /**
     * Returns the returned value of an invoked method in a contextual object
     *
     * @param context the object
     * @param name    the name of the method
     * @return the invokation's return value
     */
    private Object invokeMethod(Object context, String name) {
        try {
            Class clazz = context.getClass();
            if (context instanceof Class)
                clazz = (Class) context;
            Method method = clazz.getMethod(name);
            if (!method.isAccessible())
                method.setAccessible(true);
            return method.invoke(context);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the value of a field in a contextual object
     *
     * @param context the object
     * @param name    the name of the field
     * @return the field's value in context
     */
    private Object invokeField(Object context, String name) {
        try {
            Field field = context.getClass().getField(name);
            if (!field.isAccessible())
                field.setAccessible(true);
            return field.get(context);
        } catch (Exception e) {

        }
        return null;
    }

    private Object invokeClass(String name) {
        if (name.endsWith("."))
            name = name.substring(0, name.length() - 1);
        try {
            return (Class) ClassLoader.getSystemClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {

            return null;
        }
    }
}
