package me.kodysimpson.chunkcollector.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.BiFunction;

@Target(/*{*/ElementType.FIELD/*, ElementType.LOCAL_VARIABLE}*/)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {

    String valuePath();
    boolean translateChatColor() default true;
    ReturnType returnType() default ReturnType.STRING;
}


