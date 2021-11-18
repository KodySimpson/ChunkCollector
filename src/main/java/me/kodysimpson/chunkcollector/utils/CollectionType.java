package me.kodysimpson.chunkcollector.utils;

public enum CollectionType {

    DROP, CROP, ORE;

    public String getAsString(){
        return switch (this) {
            case DROP -> "DROP";
            case CROP -> "CROP";
            case ORE -> "ORE";
        };
    }

}
