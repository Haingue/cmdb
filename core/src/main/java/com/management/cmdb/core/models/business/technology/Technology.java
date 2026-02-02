package com.management.cmdb.core.models.business.technology;

import com.management.cmdb.core.models.business.constant.TechnologyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Technology {

    private String name;
    private String description;
    private TechnologyType type;
    private String programmingLanguage;
    private Version minimalVersion;
    private Version targetVersion;
    private Version lastVersion;

    public boolean needsUpdate(Version version) {
        return 0 <= minimalVersion.compareTo(version);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Technology) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.programmingLanguage, that.programmingLanguage) &&
                Objects.equals(this.minimalVersion, that.minimalVersion) &&
                Objects.equals(this.targetVersion, that.targetVersion) &&
                Objects.equals(this.lastVersion, that.lastVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, type, programmingLanguage, minimalVersion, targetVersion, lastVersion);
    }

    @Override
    public String toString() {
        return "Technology[" +
                "name=" + name + ", " +
                "description=" + description + ", " +
                "type=" + type + ", " +
                "programmingLanguage=" + programmingLanguage + ", " +
                "minimalVersion=" + minimalVersion + ", " +
                "targetVersion=" + targetVersion + ", " +
                "lastVersion=" + lastVersion + ']';
    }

}
