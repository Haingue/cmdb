package com.management.cartography.core.models.business.technology;

import java.util.Objects;

public class Version implements Comparable {

    int major;
    int minor;
    int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version fromString(String version) {
        if (!version.matches("[0-9]+.[0-9]+.[0-9]+")) {
            throw new IllegalArgumentException(version);
        }
        String[] token = version.trim().split("\\.");
        return new Version(
                Integer.parseInt(token[0]),
                Integer.parseInt(token[1]),
                Integer.parseInt(token[2])
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Version version)) return false;
        return major == version.major && minor == version.minor && patch == version.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Version version)) return -1;
        if (major == version.major) {
            if (minor == version.minor) return patch - version.patch;
            return minor - version.minor;
        }
        return major - version.major;
    }
}
