package com.geega.bsc.id.common.network;

import java.security.Principal;

public class KafkaPrincipal implements Principal {
    public static final String SEPARATOR = ":";
    public static final String USER_TYPE = "User";
    public static final KafkaPrincipal ANONYMOUS = new KafkaPrincipal("User", "ANONYMOUS");
    private String principalType;
    private String name;

    public KafkaPrincipal(String principalType, String name) {
        if (principalType != null && name != null) {
            this.principalType = principalType;
            this.name = name;
        } else {
            throw new IllegalArgumentException("principalType and name can not be null");
        }
    }

    public static KafkaPrincipal fromString(String str) {
        if (str != null && !str.isEmpty()) {
            String[] split = str.split(":", 2);
            if (split != null && split.length == 2) {
                return new KafkaPrincipal(split[0], split[1]);
            } else {
                throw new IllegalArgumentException("expected a string in format principalType:principalName but got " + str);
            }
        } else {
            throw new IllegalArgumentException("expected a string in format principalType:principalName but got " + str);
        }
    }

    public String toString() {
        return this.principalType + ":" + this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof KafkaPrincipal)) {
            return false;
        } else {
            KafkaPrincipal that = (KafkaPrincipal)o;
            return !this.principalType.equals(that.principalType) ? false : this.name.equals(that.name);
        }
    }

    public int hashCode() {
        int result = this.principalType.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    public String getName() {
        return this.name;
    }

    public String getPrincipalType() {
        return this.principalType;
    }
}