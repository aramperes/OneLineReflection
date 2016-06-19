package ca.momoperes.onelinereflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String[] sections = splitUpper();
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i];
            if (section.equals("$") || section.equals("this")) {
                cxt = context;
            } else if (section.startsWith("\"") && section.endsWith("\"")) {
                section = section.substring(1, section.length() - 1);
                if (i == sections.length - 1)
                    return section;
                cxt = section;
            } else if (section.contains("(") && section.contains(")")) { // Method
                String name = getMethodName(section);
                String[] parameters = getMethodParams(section);
                //System.out.println(name + " " + Arrays.toString(parameters) + " " + parameters.length);
                if (i == sections.length - 1)
                    return invokeMethod(cxt, name, parameters);
                cxt = invokeMethod(cxt, name, parameters);
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
    private Object invokeMethod(Object context, String name, String[] parameters) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            for (int i = 0; i < parameters.length; i++) {
                Object result = new ReflectionProcessor(parameters[i], this.context).process();
                if (result != null) {
                    params.add(result);
                }
            }
            Class[] classes = new Class[params.size()];
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                classes[i] = param.getClass();
            }
            Class clazz = context.getClass();
            if (context instanceof Class)
                clazz = (Class) context;
            //System.out.println(name + " " + classes.length + " " + params.size());
            Method method = clazz.getMethod(name, classes);
            if (!method.isAccessible())
                method.setAccessible(true);
            return method.invoke(context, params.toArray(new Object[params.size()]));
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

    private String[] getMethodParams(String section) {
        int level = 0;
        boolean inString = false;
        String current = "";
        List<String> parameters = new ArrayList<>();

        char[] charArray = section.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '(' && !inString) {
                level++;
                if (level > 1) {
                    current += c;
                }
                continue;
            }
            if (c == ')' && !inString) {
                level--;
                if (level == 0) {
                    parameters.add(current);
                    current = "";
                } else {
                    current += c;
                }
                continue;
            }
            if (level == 1 && c == ',' && !inString) {
                parameters.add(current);
                current = "";
                continue;
            }
            if (!inString && c == ' ')
                continue;
            if (c == '\"' && section.charAt(i - 1) != '\\') {
                inString = !inString;
            }
            if (level > 0)
                current += c;
        }
        return parameters.toArray(new String[parameters.size()]);
    }

    private String getMethodName(String section) {
        StringBuilder builder = new StringBuilder();
        for (char c : section.toCharArray()) {
            if (c == '(')
                return builder.toString();
            builder.append(c);
        }
        return builder.toString();
    }

    private String[] splitUpper() {
        ArrayList<String> sections = new ArrayList<>();
        String current = "";
        int level = 0;
        for (char c : line.toCharArray()) {
            if (level == 0 && c == '.') {
                sections.add(current);
                current = "";
                continue;
            }
            if (c == '(') {
                level++;
            }
            if (c == ')') {
                level--;
            }

            current += c;
        }
        if (current != "")
            sections.add(current);
        return sections.toArray(new String[sections.size()]);
    }
}
