package me.kodysimpson.chunkcollector.config;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Reflection {

    public static void doReflection(Class<?> mainClass, YamlConfiguration config) {
        for (Field field : mainClass.getDeclaredFields()) {
            field.setAccessible(true);
            for (ConfigValue annotation : field.getAnnotationsByType(ConfigValue.class)) {
                ReturnType returnType = ReturnType.getReturnType(field.getType());
                if (returnType == null)
                    returnType = annotation.returnType();
                if (returnType == ReturnType.NULL_LIST) {
                    System.out.println(ChatColor.RED + "ERROR! The annotation for field: " + field.getName()
                            + " is null! You must specify the returnType if your field is a list! Please use 'returnType = <whatever>_LIST' in your annotation.");
                    return;
                }
                final String path = annotation.valuePath();
                try {
                    field.set(mainClass, returnType.getFunc(config, path));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
