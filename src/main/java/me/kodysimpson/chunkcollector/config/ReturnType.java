package me.kodysimpson.chunkcollector.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.BiFunction;

public enum ReturnType {
    STRING(String.class, YamlConfiguration::getString),
    INT(int.class, YamlConfiguration::getInt),
    LONG(long.class, YamlConfiguration::getLong),
    DOUBLE(double.class, YamlConfiguration::getDouble),
    BOOLEAN(boolean.class, YamlConfiguration::getBoolean),
    ITEMSTACK(ItemStack.class, YamlConfiguration::getItemStack),
    LOCATION(Location.class, YamlConfiguration::getLocation),
    VECTOR(Vector.class, YamlConfiguration::getVector),
    OBJECT(Object.class, YamlConfiguration::get),
    LIST(List.class, YamlConfiguration::getList),
    CHAR_LIST(YamlConfiguration::getCharacterList),
    STRING_LIST(YamlConfiguration::getStringList),
    DOUBLE_LIST(YamlConfiguration::getDoubleList),
    SHORT_LIST(YamlConfiguration::getShortList),
    FLOAT_LIST(YamlConfiguration::getFloatList),
    BOOLEAN_LIST(YamlConfiguration::getBooleanList),
    INTEGER_LIST(YamlConfiguration::getIntegerList),
    LONG_LIST(YamlConfiguration::getLongList),
    BYTE_LIST(YamlConfiguration::getByteList),
    NULL_LIST(YamlConfiguration::get);

    public Class<?> returnType;

    private BiFunction<YamlConfiguration, String, ?> func;
    private ReturnType(BiFunction<YamlConfiguration, String, ?> func) {
        this.func = func;
    }
    private ReturnType(Class<?> returnType, BiFunction<YamlConfiguration, String, ?> func) {
        this.returnType = returnType;
        this.func = func;
    }

    public Object getFunc(YamlConfiguration conf, String path) {
        return func.apply(conf, path);
    }

    public static ReturnType getReturnType(Class<?> returnType) {
        for(ReturnType type : ReturnType.values()) {
            if(type.returnType.equals(returnType)) {
                return type;
            }
        }
        return ReturnType.NULL_LIST;
    }
}
