package com.ruta.api.model;

import java.util.Objects;

public class Connection {
    private final String source;
    private final String target;
    private final int time;

    public Connection(String source, String target, int time) {
        this.source = source;
        this.target = target;
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Connection that = (Connection) obj;
        return time == that.time &&
               Objects.equals(source, that.source) &&
               Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, time);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", time=" + time +
                '}';
    }
} 